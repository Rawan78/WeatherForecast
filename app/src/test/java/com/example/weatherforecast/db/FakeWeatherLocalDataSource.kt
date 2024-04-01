package com.example.weatherforecast.db

import com.example.weatherforecast.model.FavoriteCity
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.modelForAlerts.AlertDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeWeatherLocalDataSource: WeatherLocalDataSource {

    var fakeFavCities = MutableStateFlow<List<FavoriteCity>>(emptyList())
    var fakeAlerts = MutableStateFlow<List<AlertDTO>>(emptyList())
    override fun getFavCities(): Flow<List<FavoriteCity>> {
        return fakeFavCities
    }

    override suspend fun addToFav(favoriteCity: FavoriteCity) {
        fakeFavCities.value += favoriteCity
    }

    override suspend fun removeFromFav(favoriteCity: FavoriteCity) {
        fakeFavCities.value = fakeFavCities.value - favoriteCity
    }

    override fun getAllAlerts(): Flow<List<AlertDTO>> {
        return fakeAlerts
    }

    override suspend fun addToAlerts(alertDTO: AlertDTO) {
        fakeAlerts.value += alertDTO
    }

    override suspend fun removeFromAlerts(alertDTO: AlertDTO) {
        fakeAlerts.value = fakeAlerts.value - alertDTO
    }

    override fun getAllStoredWeather(): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun addCurrentWeather(weatherResponse: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun removeAllWeather() {
        TODO("Not yet implemented")
    }
}