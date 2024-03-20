package com.example.weatherforecast.HomeScreen.view

import android.Manifest
import android.content.pm.PackageManager

import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.R
import com.example.weatherforecast.network.*
import com.example.weatherforecast.model.*
import com.example.weatherforecast.HomeScreen.viewModel.*

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import com.example.weatherforecast.db.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.weatherforecast.databinding.FragmentHomeScreenBinding



class HomeScreenFragment : Fragment() {

    private val TAG = "HomeScreenFragment"

    private var fusedClient: FusedLocationProviderClient? = null

    val locationRequestID =5

    private lateinit var homeScreenViewModelFactory: HomeScreenViewModelFactory
    private lateinit var homeScreenViewModel: HomeScreenViewModel
    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter
    private lateinit var recyclerViewHourlyForecast : RecyclerView

    private lateinit var fiveDayForecastAdapter: FiveDaysForecastAdapter
    private lateinit var recyclerViewFiveDayForecast : RecyclerView

    private lateinit var binding: FragmentHomeScreenBinding



    lateinit var currentTemperature: TextView
    lateinit var currentDate: TextView
    lateinit var currentTime : TextView
    lateinit var currentDescription: TextView
    lateinit var currentCity: TextView
    lateinit var currentIcon : ImageView
    lateinit var currentWindSpeed : TextView
    lateinit var currentHumidity : TextView
    lateinit var currentPressure : TextView
    lateinit var currentClouds :TextView
    lateinit var currentSunrise : TextView
    lateinit var currentSunset : TextView
    lateinit var progressBarHome: ProgressBar

    var latitude : Double = 0.0
    var longitude :Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_screen, container, false)

        currentTemperature = view.findViewById(R.id.tv_current_temperature)
        currentDate = view.findViewById(R.id.tv_current_date)
        currentTime = view.findViewById(R.id.tv_current_time)
        currentDescription = view.findViewById(R.id.tv_current_weather_description)
        currentCity = view.findViewById(R.id.tv_current_city)
        currentIcon = view.findViewById(R.id.iv_icon_weather_status)
        currentWindSpeed = view.findViewById(R.id.tv_current_wind_speed)
        currentHumidity = view.findViewById(R.id.tv_current_Humidity)
        currentPressure = view.findViewById(R.id.tv_current_pressure)
        currentClouds = view.findViewById(R.id.tv_current_clouds)
        currentSunrise = view.findViewById(R.id.tv_current_sunrise)
        currentSunset = view.findViewById(R.id.tv_current_sunset)
        recyclerViewHourlyForecast = view.findViewById(R.id.rv_hourly_forecast)
        recyclerViewFiveDayForecast = view.findViewById(R.id.rv_5_day_forecast)
        progressBarHome = view.findViewById(R.id.progressBarHome)

        //fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        Log.i(TAG, "onCreateView: ")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        homeScreenViewModelFactory = HomeScreenViewModelFactory(WeatherRepositoryImpl.getInstance
            (WeatherRemoteDataSourceImpl.getInstance() ,  WeatherLocalDataSourceImpl(requireContext())))

        homeScreenViewModel = ViewModelProvider(this, homeScreenViewModelFactory).get(HomeScreenViewModel::class.java)

        lifecycleScope.launch{
            homeScreenViewModel.currentWeather.collectLatest{result ->
                when (result){
                    is WeatherState.Loading -> {
                        progressBarHome.visibility = View.VISIBLE
                        //recyclerViewFavoriteCities.visibility = View.GONE

                        Log.i(TAG, "onCreate: loading")
                    }
                    is WeatherState.Success -> {
                        updateUI(result.weatherResponse)
                        progressBarHome.visibility = View.GONE
                       // recyclerViewFavoriteCities.visibility = View.VISIBLE
                       // favoriteCityAdapter.setCities(result.favoriteCity)
                    }
                    else -> {
                        progressBarHome.visibility = View.GONE
                        Toast.makeText(requireContext(), "Failed to fetch cities", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        hourlyForecastAdapter = HourlyForecastAdapter()

        recyclerViewHourlyForecast.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyForecastAdapter
        }

        fiveDayForecastAdapter = FiveDaysForecastAdapter()

        recyclerViewFiveDayForecast.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = fiveDayForecastAdapter
        }

        requestLocationUpdates()

    }

    private fun updateUI(weatherResponse: WeatherResponse) {

        val temperatureFahrenheit = weatherResponse.list[0].main?.temp
        val temperatureCelsius = temperatureFahrenheit?.minus(273.15)?.toInt()
        val temperatureFormatted = temperatureCelsius?.toString()

        currentTemperature.text = "${temperatureFormatted}°C"

        val inputDate = SimpleDateFormat("yyyy-MM-dd HH:mm" , Locale.getDefault())
        val currDate = inputDate.parse(weatherResponse.list[0].dt_txt)
        val outputDate = SimpleDateFormat("d MMMM EEEE" , Locale.getDefault())
        val dateAndDayName = outputDate.format(currDate)
        currentDate.text = dateAndDayName

        val calendar = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm")
        val formattedTime = timeFormat.format(calendar.time)
        currentTime.text = formattedTime

        Log.i(TAG, "updateUI: currentDate : ${currentDate.text}  , currentTime : ${currentTime.text}")

        currentDescription.text = weatherResponse.list[0].weather.firstOrNull()?.description ?: ""
        currentCity.text = weatherResponse.city.name

        // Set Current wind speed and humidity
        val windSpeed = weatherResponse.list[0].wind?.speed ?: 0.0
        val humidity = weatherResponse.list[0].main?.humidity ?: 0

        currentWindSpeed.text = "${windSpeed} m/s"
        currentHumidity.text = "${humidity}%"

        // Set Current pressure and clouds

        val pressure = weatherResponse.list[0].main?.pressure ?: 0
        val cloudiness = weatherResponse.list[0].clouds?.all ?: 0

        currentPressure.text = "${pressure} hPa"
        currentClouds.text = "${cloudiness}%"

        // Convert Unix timestamps to HH:mm format for sunrise and sunset
        val sunriseTimeMillis = weatherResponse.city.sunrise * 1000L
        val sunsetTimeMillis = weatherResponse.city.sunset * 1000L

        val sunriseDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sunsetDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val formattedSunriseTime = sunriseDateFormat.format(Date(sunriseTimeMillis))
        val formattedSunsetTime = sunsetDateFormat.format(Date(sunsetTimeMillis))

        currentSunrise.text = formattedSunriseTime
        currentSunset.text = formattedSunsetTime

        Log.i(TAG, "updateUI: sunrise : ${weatherResponse.city.sunrise}   ,, sunset : ${weatherResponse.city.sunset}")

        // Set weather icon
        val weatherIconId = weatherResponse.list[0].weather.firstOrNull()?.icon
        val iconResourceId = getWeatherIconResourceId(weatherIconId)
        if (iconResourceId != null) {
            currentIcon.setImageResource(iconResourceId)
        }

//        Glide.with(this@HomeScreenFragment)
//            .load("https://openweathermap.org/img/wn/${weatherResponse.list[0].weather[0].icon}@2x.png")
//            .into(currentIcon)

        hourlyForecastAdapter.setList(weatherResponse.list)
        fiveDayForecastAdapter.setList(weatherResponse.list)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun requestLocationUpdates() {
        val locationRequest: LocationRequest = LocationRequest.Builder(1000).apply {
            setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
        }.build()

        val callback: LocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    latitude = location.latitude
                    longitude = location.longitude

                    Log.i(TAG, "onLocationResult: latitude = $latitude  , longitude = $longitude")

                    // stop receiving location updates after receiving the first location
                     fusedClient?.removeLocationUpdates(this)

                    // Retrieve weather data with latitude and longitude
                    homeScreenViewModel.getCurrentWeather(latitude, longitude)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                locationRequestID
            )
        } else {
            fusedClient?.requestLocationUpdates(locationRequest, callback, Looper.myLooper())
        }
    }
}