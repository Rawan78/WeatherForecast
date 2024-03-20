package com.example.weatherforecast.db

import com.example.weatherforecast.model.*
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
     fun getFavCities(): Flow<List<FavoriteCity>>
    suspend fun addToFav(favoriteCity: FavoriteCity)
    suspend fun removeFromFav(favoriteCity: FavoriteCity)
}