package com.example.weatherforecast.modelForAlerts

import com.google.gson.annotations.SerializedName


data class WeatherAlertResponse(
    @SerializedName("hourly")
    val hourlylist: List<Hourly>,

    @SerializedName("daily")
    val dailyList: List<Daily>,

    @SerializedName("minutely")
    val minutelyList : List<Minutely>,

    @SerializedName("current")
    val current: Current,

    @SerializedName("alerts")
    val alerts :List<Alerts>,

)
