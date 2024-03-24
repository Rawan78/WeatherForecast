package com.example.weatherforecast.network

import com.example.weatherforecast.modelForAlerts.WeatherAlertResponse

sealed class WeatherAlertState() {
    data class Success(val weatherAlertResponse: WeatherAlertResponse ) : WeatherAlertState()
    data class Failure(val msg: Throwable) : WeatherAlertState()
    object Loading : WeatherAlertState()
}