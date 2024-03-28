package com.example.weatherforecast.FavoriteScreen.view

import com.example.weatherforecast.model.FavoriteCity

interface OnFavCityClickListener {
    fun onFavCityClick(favoriteCity : FavoriteCity)
    fun onFavCityClickForDetails(favoriteCity : FavoriteCity)

}