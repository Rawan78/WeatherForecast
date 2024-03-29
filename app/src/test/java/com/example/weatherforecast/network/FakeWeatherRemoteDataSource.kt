package com.example.weatherforecast.network

import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.modelForAlerts.WeatherAlertResponse

class FakeWeatherRemoteDataSource(var data: MutableList<WeatherResponse> = mutableListOf() ): WeatherRemoteDataSource {
    override suspend fun getCurrentWeatherOverNetwork(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): WeatherResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentWeatherAlertsOverNetwork(
        lat: Double,
        lon: Double
    ): WeatherAlertResponse {
        TODO("Not yet implemented")
    }
}