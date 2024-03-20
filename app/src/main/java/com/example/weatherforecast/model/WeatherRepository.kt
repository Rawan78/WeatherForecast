package com.example.weatherforecast.model

import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    //From Retrofit
    suspend fun getCurrentWeather(lat:Double , lon:Double): Flow<WeatherResponse>


    //From ROOM
    suspend fun getFavCitiesFromRoom(): Flow<List<FavoriteCity>>
    suspend fun insertToFav(favoriteCity: FavoriteCity)
    suspend fun deleteFromFav(favoriteCity: FavoriteCity)
}