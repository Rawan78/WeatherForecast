package com.example.weatherforecast.HomeScreen.view

import android.content.Context
import android.util.Log
import com.example.weatherforecast.WeatherList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.weatherforecast.databinding.ItemHourlyForecastBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherforecast.SharedPrefs
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.roundToInt

class HourlyForecastAdapter : RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder>(){

    lateinit var sharedPrefs : SharedPrefs

    private val TAG = "HourlyForecastAdapter"

    lateinit var binding: ItemHourlyForecastBinding

    private var listOfTadayWeather = mutableListOf<WeatherList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemHourlyForecastBinding.inflate(inflater, parent, false)
        sharedPrefs = SharedPrefs.getInstance(parent.context)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listOfTadayWeather.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todayForecast = listOfTadayWeather[position]

        // Extracting time from dt_txt
        val time = todayForecast.dt_txt?.substring(11, 16)
        holder.binding.textViewTime.text = time

        // Display temp_min and temp_max in Celsius
        val temperature = todayForecast.main?.temp ?: 0.0

        val temperatureSymbol = getTemperatureSymbol()

        holder.binding.textViewTemperature.text = "${temperature.roundToInt()} $temperatureSymbol"


        Log.i(TAG, "onBindViewHolder: time , formatted Time $time , timeOfApi $temperature °C")


        // Set weather icon based on weather condition
        val weatherIcon = todayForecast.weather.firstOrNull()?.icon
        val iconResourceId = getWeatherIconResourceId(weatherIcon)
        if (iconResourceId != null) {
            holder.binding.imageViewCondition.setImageResource(iconResourceId)
        }

//        val weatherIcon = todayForecast.weather?.getOrNull(0)?.icon
//        weatherIcon?.let {
//            Glide.with(holder.itemView.context)
//                .load("https://openweathermap.org/img/wn/$it@2x.png")
//                .into(holder.binding.imageViewCondition)
//        }
    }

    fun setList(listOfTaday: List<WeatherList>) {
        val uniqueDates = HashSet<String>()

        val filteredList = listOfTaday.filter { entry ->
            val dateParts = entry.dt_txt.split(" ")[1]
            if (uniqueDates.contains(dateParts)) {
                false
            } else {
                uniqueDates.add(dateParts)
                true
            }
        }

        listOfTadayWeather = filteredList.toMutableList()
        notifyDataSetChanged()
    }

    inner class ViewHolder(var binding: ItemHourlyForecastBinding): RecyclerView.ViewHolder(binding.root)

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