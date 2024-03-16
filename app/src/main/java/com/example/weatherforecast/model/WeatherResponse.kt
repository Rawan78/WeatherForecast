package com.example.weatherforecast.model
import com.example.weatherforecast.WeatherList
import com.example.weatherforecast.City

data class WeatherResponse (
    val list: List<WeatherList>,
    val  city: City
)