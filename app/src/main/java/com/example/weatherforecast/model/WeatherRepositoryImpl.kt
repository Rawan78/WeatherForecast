package com.example.weatherforecast.model

import android.util.Log
import com.example.weatherforecast.WeatherList
import com.example.weatherforecast.network.*
import com.example.weatherforecast.db.*
import com.example.weatherforecast.modelForAlerts.AlertDTO
import com.example.weatherforecast.modelForAlerts.WeatherAlertResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepositoryImpl private constructor(
    private val remoteSource: WeatherRemoteDataSource  ,
    private val localSource: WeatherLocalDataSource
)
    : WeatherRepository{

    companion object {
        @Volatile
        private var instance: WeatherRepositoryImpl? = null

        fun getInstance(remoteSource: WeatherRemoteDataSource , localSource: WeatherLocalDataSource): WeatherRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: WeatherRepositoryImpl(remoteSource , localSource).also { instance = it }
            }
        }

    }


    override suspend fun getCurrentWeather(lat: Double, lon: Double , units: String , lang:String): Flow<WeatherResponse> {
        val response = remoteSource.getCurrentWeatherOverNetwork(lat, lon  , units , lang)
        return flow {
            emit(response)
        }
    }

    override suspend fun getWeatherAlerts(
        lat: Double,
        lon: Double
    ): Flow<WeatherAlertResponse> {
        val response = remoteSource.getCurrentWeatherAlertsOverNetwork(lat, lon )
        return flow {
            emit(response)
        }
    }

    override suspend fun getFavCitiesFromRoom(): Flow<List<FavoriteCity>> {
        return localSource.getFavCities()
    }

    override suspend fun insertToFav(favoriteCity: FavoriteCity) {
        localSource.addToFav(favoriteCity)
    }

    override suspend fun deleteFromFav(favoriteCity: FavoriteCity) {
        localSource.removeFromFav(favoriteCity)
    }

    override suspend fun getAllAlertsFromRoom(): Flow<List<AlertDTO>> {
        return localSource.getAllAlerts()
    }

    override suspend fun insertToAlerts(alertDTO: AlertDTO) {
        localSource.addToAlerts(alertDTO)
    }

    override suspend fun deleteFromAlerts(alertDTO: AlertDTO) {
        localSource.removeFromAlerts(alertDTO)
    }

    override fun getAllCurrentWeatherFromRoom(): Flow<WeatherResponse> {
        return localSource.getAllStoredWeather()
    }

    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        localSource.addCurrentWeather(weatherResponse)
    }

    override suspend fun deleteStoredCurrentWeather() {
        localSource.removeAllWeather()
    }


}