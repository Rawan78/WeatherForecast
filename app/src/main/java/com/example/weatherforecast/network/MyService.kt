package com.example.weatherforecast.network

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.modelForAlerts.WeatherAlertResponse


interface MyService {
    @GET("2.5/forecast")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double ,
        @Query("lon") lon: Double ,
        @Query("units") units : String,
        @Query("lang") lang : String,
        @Query("appid")
        appid: String = "d8624de82ec00c03cb20e4952badd072"
    ): WeatherResponse


    @GET("forecast")
    suspend fun getWeatherByCity(
        @Query("q")
        city: String,
        @Query("appid")
        appid: String = "d8624de82ec00c03cb20e4952badd072"

    ): WeatherResponse

    @GET("3.0/onecall?")
    suspend fun getWeatherAlerts(
        @Query("lat") lat: Double ,
        @Query("lon") lon: Double ,
        @Query("appid")
        appid: String = "d8624de82ec00c03cb20e4952badd072"

    ): WeatherAlertResponse
}