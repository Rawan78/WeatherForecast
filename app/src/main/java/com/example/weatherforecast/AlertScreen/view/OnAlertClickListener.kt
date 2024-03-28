package com.example.weatherforecast.AlertScreen.view

import com.example.weatherforecast.model.FavoriteCity
import com.example.weatherforecast.modelForAlerts.*
interface OnAlertClickListener {
    fun onAlertClick(alertDTO: AlertDTO )
}