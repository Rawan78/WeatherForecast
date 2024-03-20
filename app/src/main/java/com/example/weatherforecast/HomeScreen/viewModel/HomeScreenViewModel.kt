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
import com.example.weatherforecast.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect


class HomeScreenViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val _currentWeather: MutableStateFlow<WeatherState> = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val currentWeather : StateFlow<WeatherState> = _currentWeather

    //Over Network
    fun getCurrentWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(latitude, longitude)
                .catch {
                    _currentWeather.value = WeatherState.Failure(it)
                }
                .collect{
                    data ->
                        _currentWeather.value = WeatherState.Success(data)
                }
        }
    }
}