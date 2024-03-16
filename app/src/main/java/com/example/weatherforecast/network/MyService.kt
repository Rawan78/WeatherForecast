package com.example.weatherforecast.network

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import com.example.weatherforecast.ForeCast
import com.example.weatherforecast.model.WeatherResponse


interface MyService {
    @GET("forecast")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double ,
        @Query("lon") lon: Double ,
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
}