package com.example.weatherforecast.HomeScreen.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

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
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.AlertScreen.viewModel.AlertScreenViewModel
import com.example.weatherforecast.AlertScreen.viewModel.AlertScreenViewModelFactory
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

import com.example.weatherforecast.modelForAlerts.*
import com.example.weatherforecast.SharedPrefs
import kotlin.math.roundToInt


class HomeScreenFragment : Fragment() {

    lateinit var sharedPrefs : SharedPrefs


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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = SharedPrefs.getInstance(requireContext())

        setLocale(sharedPrefs.getLanguage() ?: "en")

        val locationMode = sharedPrefs.getLocationMode()


        //val windSpeedPreference = sharedPrefs.getWindSpeedPreference()

        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        homeScreenViewModelFactory = HomeScreenViewModelFactory(WeatherRepositoryImpl.getInstance
            (WeatherRemoteDataSourceImpl.getInstance() ,  WeatherLocalDataSourceImpl(requireContext())))

        homeScreenViewModel = ViewModelProvider(this, homeScreenViewModelFactory).get(HomeScreenViewModel::class.java)



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

        if (isNetworkAvailable()) {
            homeScreenViewModel.removeAllStoredWeather()


            if (locationMode == "GPS") {
                requestLocationUpdates()
            } else if (locationMode == "Map") {
                // Retrieve selected location from SharedPreferences and show weather
                val latitude = sharedPrefs.getLatitude()
                val longitude = sharedPrefs.getLongitude()
                val temperatureUnits = getTemperatureUnits()
                Log.i(TAG, "onViewCreated: sharedPrefs.getLatitude() : $latitude , sharedPrefs.getLongitude() : $longitude")
                val selectedLanguage = sharedPrefs.getLanguage() ?: "en"
                homeScreenViewModel.getCurrentWeather(
                    latitude,
                    longitude,
                    units = temperatureUnits,
                    lang = selectedLanguage
                )
                setLocale(selectedLanguage)
            } else {
                requestLocationUpdates()
//                Toast.makeText(
//                    requireContext(),
//                    "Please select a location mode",
//                    Toast.LENGTH_SHORT
//                ).show()
            }

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
                            homeScreenViewModel.insertCurrentWeather(result.weatherResponse)
                            // recyclerViewFavoriteCities.visibility = View.VISIBLE
                            // favoriteCityAdapter.setCities(result.favoriteCity)
                        }
                        else -> {
                            progressBarHome.visibility = View.GONE
                            Toast.makeText(requireContext(), "Failed to fetch data from internet", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }else {
            Log.i(TAG, "onViewCreated: disconnected to internet")
            lifecycleScope.launch{
                homeScreenViewModel.currentWeather.collectLatest{result ->
                    when (result){
                        is WeatherState.Loading -> {
                           progressBarHome.visibility = View.VISIBLE                         
                        //recyclerViewFavoriteCities.visibility = View.GONE
                            Log.i(TAG, "onViewCreated: loading data from database ")
                        }
                        is WeatherState.Success -> {
                            Log.i(TAG, "onViewCreated: successed retrieving data from Room ")
                            updateUI(result.weatherResponse)
                            progressBarHome.visibility = View.GONE

                            Log.i(TAG, "onViewCreated: ${result.weatherResponse.city.name}")
                            
                            // recyclerViewFavoriteCities.visibility = View.VISIBLE
                            // favoriteCityAdapter.setCities(result.favoriteCity)
                        }
                        else -> {
                            progressBarHome.visibility = View.GONE
                            Toast.makeText(requireContext(), "Failed to fetch data from Room", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            homeScreenViewModel.getStoredWeatherFromRoom()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    private fun updateUI(weatherResponse: WeatherResponse) {

        val temperature = weatherResponse.list[0].main?.temp ?: 0.0
        val temperatureSymbol = getTemperatureSymbol()

        currentTemperature.text = "${temperature.roundToInt()}°$temperatureSymbol"

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
        val humidity = weatherResponse.list[0].main?.humidity ?: 0

        currentHumidity.text = "${humidity}%"

        // Convert wind speed from m/s to the preferred unit (e.g., Miles/Hour or Meter/Sec) based on user preference
        val windSpeedMetersPerSec = weatherResponse.list[0].wind?.speed ?: 0.0
        val windSpeedPreference = sharedPrefs.getWindSpeedPreference()

        Log.i(TAG, "updateUI: getWindSpeedPreference : ${sharedPrefs.getWindSpeedPreference()}")

        val convertedWindSpeed = when (windSpeedPreference) {
            "Miles/Hour" -> convertMetersPerSecToMilesPerHour(windSpeedMetersPerSec)
            "Meter/Sec" -> windSpeedMetersPerSec
            else -> windSpeedMetersPerSec
        }

        // Update the wind speed TextView with the converted wind speed
        currentWindSpeed.text = "$convertedWindSpeed $windSpeedPreference"

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

    //For Wind Speed
    private fun convertMetersPerSecToMilesPerHour(metersPerSec: Double): String {
        val result = metersPerSec * 2.23694
        return String.format("%.2f", result)
    }

    //For Language
    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    //For Temp
    private fun getTemperatureUnits(): String {
        val tempUnitPreference = sharedPrefs.getTemp() ?: ""
        return when (tempUnitPreference) {
            "Fahrenheit" -> "imperial"
            "Celsius" -> "metric"
            else -> ""
        }
    }

    private fun getTemperatureSymbol(): String {
        return when (sharedPrefs.getTemp()) {
            "Fahrenheit" -> "F"
            "Celsius" -> "C"
            else -> "K"
        }
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

                    var selectedLanguage = sharedPrefs.getLanguage() ?: "en"

                    // Retrieve temperature units preference
                    val temperatureUnits = getTemperatureUnits()

                    // Retrieve weather data with latitude and longitude
                    homeScreenViewModel.getCurrentWeather(latitude, longitude , units = temperatureUnits ,lang = selectedLanguage)
                    setLocale(selectedLanguage)
                    //alertScreenViewModel.getCurrentWeatherAlert(latitude, longitude)
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