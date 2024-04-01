package com.example.weatherforecast.model

import com.example.weatherforecast.modelForAlerts.AlertDTO
import com.example.weatherforecast.modelForAlerts.WeatherAlertResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherRepository: WeatherRepository {

    private val fakeWeatherData: MutableMap<Pair<Double, Double>, WeatherResponse> = mutableMapOf()
    private val fakeWeatherAlertData: MutableMap<Pair<Double, Double>, WeatherAlertResponse> = mutableMapOf()

    var fakeFavCities: MutableList<FavoriteCity> = mutableListOf()
    var fakeAlerts: MutableList<AlertDTO> = mutableListOf()

    // Add fake weather data for a specific location
    fun addWeatherData(lat: Double, lon: Double, weatherResponse: WeatherResponse) {
        fakeWeatherData[Pair(lat, lon)] = weatherResponse
    }

    // Add fake weather alert data for a specific location
    fun addWeatherAlertData(lat: Double, lon: Double, weatherAlertResponse: WeatherAlertResponse) {
        fakeWeatherAlertData[Pair(lat, lon)] = weatherAlertResponse
    }
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String,
        lang: String
    ): Flow<WeatherResponse> {
        val response = fakeWeatherData[Pair(lat, lon)]
        return flow {
            response?.let { emit(it) }
        }
    }

    override suspend fun getWeatherAlerts(lat: Double, lon: Double): Flow<WeatherAlertResponse> {
        val response = fakeWeatherAlertData[Pair(lat, lon)]
        return flow {
            response?.let { emit(it) }
        }
    }

    override suspend fun getFavCitiesFromRoom(): Flow<List<FavoriteCity>> {
        return flow { emit(fakeFavCities) }
    }

    override suspend fun insertToFav(favoriteCity: FavoriteCity) {
        fakeFavCities.add(favoriteCity)
    }

    override suspend fun deleteFromFav(favoriteCity: FavoriteCity) {
        fakeFavCities.remove(favoriteCity)
    }

    override suspend fun getAllAlertsFromRoom(): Flow<List<AlertDTO>> {
        return flow { emit(fakeAlerts) }
    }

    override suspend fun insertToAlerts(alertDTO: AlertDTO) {
        fakeAlerts.add(alertDTO)
    }

    override suspend fun deleteFromAlerts(alertDTO: AlertDTO) {
        fakeAlerts.remove(alertDTO)
    }

    override  fun getAllCurrentWeatherFromRoom(): Flow<WeatherResponse>{
        TODO("Not yet implemented")
    }

    override suspend fun insertCurrentWeather(weatherResponse: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteStoredCurrentWeather() {
        TODO("Not yet implemented")
    }
}