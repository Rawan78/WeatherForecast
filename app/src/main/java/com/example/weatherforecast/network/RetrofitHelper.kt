package com.example.weatherforecast.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper{
    const val BASE_URL = "https://api.openweathermap.org/data/"
    const val PERMISSIN_REQUEST_CODE = 123
    val retrofitInstance = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}