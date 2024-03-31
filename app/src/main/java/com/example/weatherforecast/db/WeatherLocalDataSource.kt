package com.example.weatherforecast.db

import com.example.weatherforecast.model.*
import kotlinx.coroutines.flow.Flow
import com.example.weatherforecast.modelForAlerts.*

interface WeatherLocalDataSource {

    //For Favorite
     fun getFavCities(): Flow<List<FavoriteCity>>
    suspend fun addToFav(favoriteCity: FavoriteCity)
    suspend fun removeFromFav(favoriteCity: FavoriteCity)

    //For Alert
    fun getAllAlerts(): Flow<List<AlertDTO>>
    suspend fun addToAlerts(alertDTO: AlertDTO)
    suspend fun removeFromAlerts(alertDTO: AlertDTO)

    //For stored weather
    fun getAllStoredWeather(): Flow<WeatherResponse>
    suspend fun addCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun removeAllWeather()

}