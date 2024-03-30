package com.example.weatherforecast.FavoriteScreen.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.example.weatherforecast.HomeScreen.view.*
import com.example.weatherforecast.HomeScreen.viewModel.*
import com.example.weatherforecast.R
import com.example.weatherforecast.SharedPrefs
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import com.example.weatherforecast.model.*
import com.example.weatherforecast.db.*
import com.example.weatherforecast.network.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


class CityDetailsActivity : AppCompatActivity() {

    lateinit var sharedPrefs : SharedPrefs

    private val TAG = "HomeScreenActivity"

    private var fusedClient: FusedLocationProviderClient? = null

    private lateinit var homeScreenViewModelFactory: HomeScreenViewModelFactory
    private lateinit var homeScreenViewModel: HomeScreenViewModel
    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter
    private lateinit var recyclerViewHourlyForecast: RecyclerView
    private lateinit var fiveDayForecastAdapter: FiveDaysForecastAdapter
    private lateinit var recyclerViewFiveDayForecast: RecyclerView

    lateinit var currentTemperature: TextView
    lateinit var currentDate: TextView
    lateinit var currentTime: TextView
    lateinit var currentDescription: TextView
    lateinit var currentCity: TextView
    lateinit var currentIcon: ImageView
    lateinit var currentWindSpeed: TextView
    lateinit var currentHumidity: TextView
    lateinit var currentPressure: TextView
    lateinit var currentClouds: TextView
    lateinit var currentSunrise: TextView
    lateinit var currentSunset: TextView
    lateinit var progressBarHome: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_details)

        currentTemperature = findViewById(R.id.tv_current_temperature)
        currentDate = findViewById(R.id.tv_current_date)
        currentTime = findViewById(R.id.tv_current_time)
        currentDescription = findViewById(R.id.tv_current_weather_description)
        currentCity = findViewById(R.id.tv_current_city)
        currentIcon = findViewById(R.id.iv_icon_weather_status)
        currentWindSpeed = findViewById(R.id.tv_current_wind_speed)
        currentHumidity = findViewById(R.id.tv_current_Humidity)
        currentPressure = findViewById(R.id.tv_current_pressure)
        currentClouds = findViewById(R.id.tv_current_clouds)
        currentSunrise = findViewById(R.id.tv_current_sunrise)
        currentSunset = findViewById(R.id.tv_current_sunset)
        recyclerViewHourlyForecast = findViewById(R.id.rv_hourly_forecast)
        recyclerViewFiveDayForecast = findViewById(R.id.rv_5_day_forecast)
        progressBarHome = findViewById(R.id.progressBarHome)

        sharedPrefs = SharedPrefs.getInstance(this)
        val selectedLanguage = sharedPrefs.getLanguage() ?: "en"

        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        homeScreenViewModelFactory = HomeScreenViewModelFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherRemoteDataSourceImpl.getInstance(),
                WeatherLocalDataSourceImpl(this)
            )
        )

        homeScreenViewModel =
            ViewModelProvider(this, homeScreenViewModelFactory).get(HomeScreenViewModel::class.java)

        lifecycleScope.launch {
            homeScreenViewModel.currentWeather.collectLatest { result ->
                when (result) {
                    is WeatherState.Loading -> {
                        progressBarHome.visibility = View.VISIBLE
                        Log.i(TAG, "onCreate: loading")
                    }
                    is WeatherState.Success -> {
                        updateUI(result.weatherResponse)
                        progressBarHome.visibility = View.GONE
                    }
                    else -> {
                        progressBarHome.visibility = View.GONE
                        Toast.makeText(this@CityDetailsActivity, "Failed to fetch cities", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        hourlyForecastAdapter = HourlyForecastAdapter()
        recyclerViewHourlyForecast.apply {
            layoutManager = LinearLayoutManager(this@CityDetailsActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyForecastAdapter
        }

        fiveDayForecastAdapter = FiveDaysForecastAdapter()
        recyclerViewFiveDayForecast.apply {
            layoutManager = LinearLayoutManager(this@CityDetailsActivity, LinearLayoutManager.VERTICAL, false)
            adapter = fiveDayForecastAdapter
        }

        val favoriteCity = intent.getSerializableExtra("favoriteCity") as FavoriteCity

        // Retrieve temperature units preference
        val temperatureUnits = getTemperatureUnits()

        homeScreenViewModel.getCurrentWeather(favoriteCity.lat, favoriteCity.lon, units = temperatureUnits ,lang = selectedLanguage )
        Log.i(TAG, "onCreate:  favoriteCity.lat : ${favoriteCity.lat}  , favoriteCity.lon : ${favoriteCity.lon}")

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
        //val windSpeed = weatherResponse.list[0].wind?.speed ?: 0.0
        val humidity = weatherResponse.list[0].main?.humidity ?: 0

        //currentWindSpeed.text = "${windSpeed} m/s"
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
}