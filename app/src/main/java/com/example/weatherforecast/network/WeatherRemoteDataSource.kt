package com.example.weatherforecast.network
import com.example.weatherforecast.model.*
import com.example.weatherforecast.modelForAlerts.*

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeatherOverNetwork(lat:Double , lon:Double ,units: String,lang:String): WeatherResponse

    suspend fun getCurrentWeatherAlertsOverNetwork(lat:Double , lon:Double ): WeatherAlertResponse
}