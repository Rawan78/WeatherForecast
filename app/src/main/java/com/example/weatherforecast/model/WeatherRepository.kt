package com.example.weatherforecast.model

import com.example.weatherforecast.WeatherList

interface WeatherRepository {
    suspend fun getCurrentWeather(lat:Double , lon:Double): WeatherResponse
}