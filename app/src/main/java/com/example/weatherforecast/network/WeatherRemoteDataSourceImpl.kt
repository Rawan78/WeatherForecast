package com.example.weatherforecast.network

import android.util.Log
import com.example.weatherforecast.ForeCast
import com.example.weatherforecast.model.WeatherResponse
import com.example.weatherforecast.modelForAlerts.WeatherAlertResponse
import retrofit2.Call

class WeatherRemoteDataSourceImpl private constructor() : WeatherRemoteDataSource{

    private val TAG = "WeatherRemoteDataSourceImpl"

    val myWeatherService : MyService by lazy {
        RetrofitHelper.retrofitInstance.create(MyService::class.java)
    }

    companion object{
        private var instance: WeatherRemoteDataSourceImpl? = null
        fun getInstance(): WeatherRemoteDataSourceImpl{
            return instance?: synchronized(this){
                val temp = WeatherRemoteDataSourceImpl()
                instance = temp
                temp
            }
        }
    }

    override suspend fun getCurrentWeatherOverNetwork(lat: Double, lon: Double, units: String ,lang:String): WeatherResponse{
        val response = myWeatherService.getCurrentWeather(lat, lon , units ,lang)
        Log.i(TAG, "getCurrentWeatherOverNetwork: $response")
        return response
    }

    override suspend fun getCurrentWeatherAlertsOverNetwork(
        lat: Double,
        lon: Double
    ): WeatherAlertResponse {
        val response = myWeatherService.getWeatherAlerts(lat,lon)
        Log.i(TAG, "getCurrentWeatherAlertsOverNetwork: lat : $lat , lon : $lon")
        return response
    }


}