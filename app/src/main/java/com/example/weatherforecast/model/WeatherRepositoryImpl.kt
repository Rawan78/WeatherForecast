package com.example.weatherforecast.model

import android.util.Log
import com.example.weatherforecast.WeatherList
import com.example.weatherforecast.network.*
import kotlin.math.log

class WeatherRepositoryImpl private constructor(private val remoteSource: WeatherRemoteDataSource)
    : WeatherRepository{
    private val TAG = "WeatherRepositoryImpl"

    companion object {
        @Volatile
        private var instance: WeatherRepositoryImpl? = null

        fun getInstance(remoteSource: WeatherRemoteDataSource): WeatherRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepositoryImpl(remoteSource).also { instance = it }
            }
        }

    }


    override suspend fun getCurrentWeather(lat: Double, lon: Double): WeatherResponse {
        val response = remoteSource.getCurrentWeatherOverNetwork(lat, lon)
        return response
        Log.i(TAG, "getCurrentWeather: ")
    }


}