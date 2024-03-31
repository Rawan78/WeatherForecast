package com.example.weatherforecast

import androidx.room.TypeConverter
import com.google.gson.Gson

class CityConverter {
    @TypeConverter
    fun fromString(value: String): City {
        return Gson().fromJson(value, City::class.java)
    }

    @TypeConverter
    fun fromCity(city: City): String {
        return Gson().toJson(city)
    }
}