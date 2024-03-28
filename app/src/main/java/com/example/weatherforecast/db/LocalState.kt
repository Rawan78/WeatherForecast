package com.example.weatherforecast.db

import com.example.weatherforecast.model.*
import com.example.weatherforecast.modelForAlerts.AlertDTO

sealed class LocalState() {
    data class Success(val favoriteCity: List<FavoriteCity>) : LocalState()
    data class SuccessAlert(val alertDTO: List<AlertDTO>) : LocalState()
    data class Failure(val msg: Throwable) : LocalState()
    object Loading : LocalState()

}