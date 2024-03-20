package com.example.weatherforecast.Map.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Rect
import android.location.Geocoder
import android.location.GpsStatus
import android.location.Location
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.FavoriteScreen.viewModel.FavoriteCityViewModel
import com.example.weatherforecast.FavoriteScreen.viewModel.FavoriteCityViewModelFactory

import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.ActivityOpenStreetMapBinding
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

import com.example.weatherforecast.Map.viewModel.*
import com.example.weatherforecast.model.*
import com.example.weatherforecast.network.*
import com.example.weatherforecast.db.*

import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import java.util.Locale
import com.example.weatherforecast.MainActivity


class OpenStreetMapActivity() : AppCompatActivity() , MapListener {

    private val TAG = "OpenStreetMapActivity"

    lateinit var mMap: MapView
    lateinit var controller: IMapController;
    lateinit var mMyLocationOverlay: MyLocationNewOverlay
    lateinit var binding: ActivityOpenStreetMapBinding


    private lateinit var favoriteCityViewModelFactory: FavoriteCityViewModelFactory
    private lateinit var favoriteCityViewModel: FavoriteCityViewModel



    private lateinit var pinMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOpenStreetMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )

        mMap = binding.osmmap
        mMap.setTileSource(TileSourceFactory.MAPNIK)
        mMap.mapCenter
        mMap.setMultiTouchControls(true)
        mMap.getLocalVisibleRect(Rect())


        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mMap)
        controller = mMap.controller

        mMyLocationOverlay.enableMyLocation()
        mMyLocationOverlay.enableFollowLocation()
        mMyLocationOverlay.isDrawAccuracyEnabled = true
        mMyLocationOverlay.runOnFirstFix {
            runOnUiThread {
                controller.setCenter(mMyLocationOverlay.myLocation);
                controller.animateTo(mMyLocationOverlay.myLocation)
            }
        }

        controller.setZoom(6.0)
        mMap.overlays.add(mMyLocationOverlay)
        mMap.addMapListener(this)

        pinMarker = Marker(mMap)
        binding.osmmap.overlays.add(TapOverlay())

        favoriteCityViewModelFactory = FavoriteCityViewModelFactory(
            WeatherRepositoryImpl.getInstance
                (WeatherRemoteDataSourceImpl.getInstance() , WeatherLocalDataSourceImpl(this)))

        favoriteCityViewModel = ViewModelProvider(this, favoriteCityViewModelFactory).get(
            FavoriteCityViewModel::class.java)

    }

    override fun onScroll(event: ScrollEvent?): Boolean {
//        Log.i("TAG", "onCreate:la ${event?.source?.getMapCenter()?.latitude}")
//        Log.i("TAG", "onCreate:lo ${event?.source?.getMapCenter()?.longitude}")
        return true
    }

    override fun onZoom(event: ZoomEvent?): Boolean {
        //Log.i("TAG", "onZoom zoom level: ${event?.zoomLevel}   source:  ${event?.source}")
        return false;    }

    private inner class TapOverlay : Overlay(){
        override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {

            val point = mapView?.projection?.fromPixels(e?.x?.toInt() ?: 0, e?.y?.toInt() ?: 0)

            binding.osmmap.overlays.remove(pinMarker)
            pinMarker = Marker(mapView)
            pinMarker?.position = point as GeoPoint
            binding.osmmap.overlays.add(pinMarker)
            binding.osmmap.invalidate()

            val cityName = getCityName(point.latitude, point.longitude)


            binding.fabAdd.setOnClickListener {
                if (cityName != null) {
                    val favoriteCity = FavoriteCity(cityName, point.latitude, point.longitude)
                    favoriteCityViewModel.insertCityToFavorite(favoriteCity)
                    Log.i(TAG, "onSingleTapConfirmed: favorite city : $favoriteCity ( ${favoriteCity.name} , ${favoriteCity.lat} , ${favoriteCity.lon} )")
                    Toast.makeText(this@OpenStreetMapActivity, "${favoriteCity.name} added to favorites", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@OpenStreetMapActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@OpenStreetMapActivity, "Failed to get city name", Toast.LENGTH_SHORT).show()
                }
            }
            return true
        }
    }

    fun getCityName(lat : Double , lon : Double): String?{

        val geoCoder = Geocoder(this , Locale.getDefault())
        val fullAddress = geoCoder.getFromLocation(lat, lon , 1)
        if (fullAddress != null && fullAddress.isNotEmpty()){
            val address = fullAddress[0]
            val city = address.adminArea
            Toast.makeText(this, "$city" , Toast.LENGTH_LONG).show()
            return city
        }
        return null
    }

}