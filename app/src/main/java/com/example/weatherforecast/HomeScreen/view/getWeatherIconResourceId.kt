package com.example.weatherforecast.HomeScreen.view

import com.example.weatherforecast.R

fun getWeatherIconResourceId(iconId: String?): Int {
    return when (iconId) {
        "01d" -> R.drawable.oned
        "01n" -> R.drawable.onen
        "02d" -> R.drawable.twod
        "02n" -> R.drawable.twon
        "03d", "03n" -> R.drawable.threedn
        "09d" -> R.drawable.nined
        "10d" -> R.drawable.tend
        "10n" -> R.drawable.tenn
        "11d" -> R.drawable.elevend
        "13d" -> R.drawable.thirteend
        "15d", "15n" -> R.drawable.fiftydn
        else -> R.drawable.cloudy
    }
}