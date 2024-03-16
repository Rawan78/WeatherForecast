package com.example.weatherforecast.HomeScreen.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.weatherforecast.WeatherList


class HomeScreenViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val _currentWeather: MutableLiveData<WeatherResponse> = MutableLiveData<WeatherResponse>()
    val currentWeather : LiveData<WeatherResponse> = _currentWeather

    //Over Network
    fun getCurrentWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
                val weatherList = weatherRepository.getCurrentWeather(latitude, longitude)
                _currentWeather.value = weatherList
                Log.i(TAG, "getCurrentWeather: ${weatherList}")

        }
    }
}