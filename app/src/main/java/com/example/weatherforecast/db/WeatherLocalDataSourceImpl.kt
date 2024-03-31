package com.example.weatherforecast.db

import android.content.Context
import com.example.weatherforecast.model.FavoriteCity
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.modelForAlerts.AlertDTO
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(context: Context) : WeatherLocalDataSource{

    private val favoriteDAO : FavoriteDAO by lazy {
        val db: WeatherDatabase = WeatherDatabase.getInstance(context)
        db.getFavoriteCityDao()
    }

    private val alertDAO : AlertDAO by lazy {
        val db: WeatherDatabase = WeatherDatabase.getInstance(context)
        db.getAlertsDao()
    }

    private val currentWeatherDAO : CurrentWeatherDAO by lazy {
        val db: WeatherDatabase = WeatherDatabase.getInstance(context)
        db.getWeatherDao()
    }

    override fun getFavCities(): Flow<List<FavoriteCity>> {
        return favoriteDAO.getStoredFavoriteCities()
    }

    override suspend fun addToFav(favoriteCity: FavoriteCity) {
        favoriteDAO.insertFavorite(favoriteCity)
    }

    override suspend fun removeFromFav(favoriteCity: FavoriteCity) {
        favoriteDAO.deleteFavorite(favoriteCity)
    }

    //For Alerts
    override fun getAllAlerts(): Flow<List<AlertDTO>> {
        return alertDAO.getStoredAlerts()
    }

    override suspend fun addToAlerts(alertDTO: AlertDTO) {
        alertDAO.insertAlert(alertDTO)
    }

    override suspend fun removeFromAlerts(alertDTO: AlertDTO) {
        alertDAO.deleteAlert(alertDTO)
    }

    override fun getAllStoredWeather(): Flow<WeatherResponse> {
        return currentWeatherDAO.getStoredWeather()
    }

    override suspend fun addCurrentWeather(weatherResponse: WeatherResponse) {
        currentWeatherDAO.insertAllCurrentWeather(weatherResponse)
    }

    override suspend fun removeAllWeather() {
        currentWeatherDAO.deleteAllWeather()
    }
}