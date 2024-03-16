package com.example.weatherforecast.network
import com.example.weatherforecast.model.*

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeatherOverNetwork(lat:Double , lon:Double): WeatherResponse
}