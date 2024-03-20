package com.example.weatherforecast.db

import com.example.weatherforecast.model.*

sealed class FavoriteCityState() {
    data class Success(val favoriteCity: List<FavoriteCity>) : FavoriteCityState()
    data class Failure(val msg: Throwable) : FavoriteCityState()
    object Loading : FavoriteCityState()

}