package com.example.weatherforecast.HomeScreen.view

import android.content.Context
import android.util.Log
import com.example.weatherforecast.WeatherList
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.weatherforecast.databinding.ItemHourlyForecastBinding
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar

class HourlyForecastAdapter : RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder>(){

    private val TAG = "HourlyForecastAdapter"

    lateinit var binding: ItemHourlyForecastBinding

    private val listOfTadayWeather = mutableListOf<WeatherList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemHourlyForecastBinding.inflate(inflater, parent, false)
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

        // Display temperature
        val temperatureCelsius = todayForecast.main?.temp?.minus(273.15)?.toInt()
        val temperatureFormatted = temperatureCelsius?.toString()
        holder.binding.textViewTemperature.text = "$temperatureFormatted °C"


        Log.i(TAG, "onBindViewHolder: time , formatted Time $time , timeOfApi $temperatureFormatted °C")


        // Set weather icon based on weather condition
        val weatherIcon = todayForecast.weather.firstOrNull()?.icon
        val iconResourceId = getWeatherIconResourceId(weatherIcon)
        if (iconResourceId != null) {
            holder.binding.imageViewCondition.setImageResource(iconResourceId)
        }
    }

    fun setList(listOfTaday: List<WeatherList>) {
        listOfTadayWeather.clear()
        listOfTadayWeather.addAll(listOfTaday)
        notifyDataSetChanged()
    }

    inner class ViewHolder(var binding: ItemHourlyForecastBinding): RecyclerView.ViewHolder(binding.root)

}