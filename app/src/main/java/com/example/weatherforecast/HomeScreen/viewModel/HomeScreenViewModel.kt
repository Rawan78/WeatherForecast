package com.example.weatherforecast.HomeScreen.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.db.LocalState
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
    fun getCurrentWeather(latitude: Double, longitude: Double  , units: String , lang:String) {
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(latitude, longitude, units ,lang)
                .catch {
                    _currentWeather.value = WeatherState.Failure(it)
                }
                .collect{
                    data ->
                        _currentWeather.value = WeatherState.Success(data)
                }
        }
    }


    //Room
//    fun getStoredWeatherFromRoom(){
//        viewModelScope.launch{
//            weatherRepository.getAllCurrentWeatherFromRoom()
//                .catch {
//                    _currentWeather.value = WeatherState.Failure(it)
//                }
//                .collect{
//                        data -> _currentWeather.value = WeatherState.Success(data)
//                }
//            Log.i(TAG, "getStoredWeatherFromRoom: ")
//
//        }
//    }

    // Room
    fun getStoredWeatherFromRoom() {
        viewModelScope.launch {
            weatherRepository.getAllCurrentWeatherFromRoom()
                .catch { exception ->
                    _currentWeather.value = WeatherState.Failure(exception)
                }
                .collect { data ->
                    if (data != null) {
                        _currentWeather.value = WeatherState.Success(data)
                    } else {
                        // Handle the case where data is null
                        _currentWeather.value = WeatherState.Failure(NullPointerException("Stored weather data is null"))
                    }
                }
            Log.i(TAG, "getStoredWeatherFromRoom: ")
        }
    }


    fun insertCurrentWeather(weatherResponse : WeatherResponse){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.insertCurrentWeather(weatherResponse)
            //getStoredWeatherFromRoom()
        }
    }
    fun removeAllStoredWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteStoredCurrentWeather()
            //getStoredWeatherFromRoom()
        }
    }

}