package com.example.weatherforecast.model

import kotlinx.coroutines.flow.Flow
import com.example.weatherforecast.modelForAlerts.*

interface WeatherRepository {

    //From Retrofit
    suspend fun getCurrentWeather(lat:Double , lon:Double , units: String , lang:String): Flow<WeatherResponse>

    suspend fun getWeatherAlerts(lat:Double , lon:Double ) : Flow<WeatherAlertResponse>


    //From ROOM
    suspend fun getFavCitiesFromRoom(): Flow<List<FavoriteCity>>
    suspend fun insertToFav(favoriteCity: FavoriteCity)
    suspend fun deleteFromFav(favoriteCity: FavoriteCity)
}