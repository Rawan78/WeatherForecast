package com.example.weatherforecast.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.weatherforecast.WeatherList
import com.example.weatherforecast.City
import com.example.weatherforecast.*

@Entity(tableName = "current_weather_table")
data class WeatherResponse (
    val list: List<WeatherList>,
    @PrimaryKey
    val  city: City
)
