package com.example.weatherforecast

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WeatherListConverter {
    @TypeConverter
    fun fromWeatherList(list: List<WeatherList>): String {
        val gson = Gson()
        return gson.toJson(list)
    }

    @TypeConverter
    fun toWeatherList(json: String): List<WeatherList> {
        val gson = Gson()
        val type = object : TypeToken<List<WeatherList>>() {}.type
        return gson.fromJson(json, type)
    }
}