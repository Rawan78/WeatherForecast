package com.example.weatherforecast.network

import com.example.weatherforecast.model.*


sealed class WeatherState() {
    data class Success(val weatherResponse: WeatherResponse) : WeatherState()
    data class Failure(val msg: Throwable) : WeatherState()
    object Loading : WeatherState()
}