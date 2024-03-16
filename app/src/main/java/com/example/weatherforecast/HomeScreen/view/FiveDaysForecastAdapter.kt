package com.example.weatherforecast.HomeScreen.view

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.WeatherList
import com.example.weatherforecast.databinding.ItemDayForecastInHomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FiveDaysForecastAdapter : RecyclerView.Adapter<FiveDaysForecastAdapter.ViewHolder>(){

    private val TAG = "FiveDaysForecastAdapter"

    lateinit var binding: ItemDayForecastInHomeBinding

    var listOfForecast = listOf<WeatherList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemDayForecastInHomeBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listOfForecast.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var forecastObject = listOfForecast[position]

        val dateParts = forecastObject.dt_txt.split(" ")[0]
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.parse(dateParts)

        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val formattedDay = dayFormat.format(date)

        holder.binding.textViewDate.text = formattedDay

        // Display temp_min and temp_max in Celsius
        val tempMinCelsius = forecastObject.main?.temp_min?.minus(273.15)?.toInt()
        val formattedTempMin = tempMinCelsius?.toString()

        val tempMaxCelsius = forecastObject.main?.temp_max?.minus(273.15)?.toInt()
        val formattedTempMax = tempMaxCelsius?.toString()
        holder.binding.textViewTemperature.text = "${formattedTempMin}°/${formattedTempMax}°"

        Log.i(TAG, "onBindViewHolder:$formattedTempMin°/$formattedTempMax° ")

        // Set weather icon
        val weatherIconId = forecastObject.weather.firstOrNull()?.icon
        val iconResourceId = getWeatherIconResourceId(weatherIconId)
        if (iconResourceId != null) {
            holder.binding.imageViewWeatherIcon.setImageResource(iconResourceId)
        }
    }

    fun setList(newList: List<WeatherList>) {
        // Use a hash set to store the unique dates
        val uniqueDates = HashSet<String>()

        // Filter the new list to include only the entries with unique dates
        val filteredList = newList.filter { entry ->
            val dateParts = entry.dt_txt.split(" ")[0]
            if (uniqueDates.contains(dateParts)) {
                false // Not a unique date, filter it out
            } else {
                uniqueDates.add(dateParts)
                true // Unique date, keep it
            }
        }

        listOfForecast = filteredList
        notifyDataSetChanged()
    }
    inner class ViewHolder(var binding: ItemDayForecastInHomeBinding): RecyclerView.ViewHolder(binding.root)

}