package com.example.weatherforecast.db

import android.content.Context
import com.example.weatherforecast.model.FavoriteCity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImpl(context: Context) : WeatherLocalDataSource{

    private val favoriteDAO : FavoriteDAO by lazy {
        val db: WeatherDatabase = WeatherDatabase.getInstance(context)
        db.getFavoriteCityDao()
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
}