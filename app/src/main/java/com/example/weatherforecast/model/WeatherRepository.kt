package com.example.weatherforecast.model

import kotlinx.coroutines.flow.Flow
import com.example.weatherforecast.modelForAlerts.*

interface WeatherRepository {

    //From Retrofit
    suspend fun getCurrentWeather(lat:Double , lon:Double , units: String , lang:String): Flow<WeatherResponse>

    suspend fun getWeatherAlerts(lat:Double , lon:Double ) : Flow<WeatherAlertResponse>


    //From ROOM
    // For Favorite
    suspend fun getFavCitiesFromRoom(): Flow<List<FavoriteCity>>
    suspend fun insertToFav(favoriteCity: FavoriteCity)
    suspend fun deleteFromFav(favoriteCity: FavoriteCity)

    // For Alerts
    suspend fun getAllAlertsFromRoom(): Flow<List<AlertDTO>>
    suspend fun insertToAlerts(alertDTO: AlertDTO)
    suspend fun deleteFromAlerts(alertDTO: AlertDTO)

    // For Stored Weather
     fun getAllCurrentWeatherFromRoom(): Flow<WeatherResponse>
    suspend fun insertCurrentWeather(weatherResponse: WeatherResponse)
    suspend fun deleteStoredCurrentWeather()
}