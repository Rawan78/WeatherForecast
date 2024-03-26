package com.example.weatherforecast.AlertScreen.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.db.LocalState
import com.example.weatherforecast.modelForAlerts.*
import com.example.weatherforecast.model.WeatherRepository
import com.example.weatherforecast.network.*
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AlertScreenViewModel (private val weatherRepository: WeatherRepository) : ViewModel(){

    private val TAG = "AlertScreenViewModel"

//    private val _currentWeatherAlert: MutableStateFlow<WeatherAlertState> = MutableStateFlow<WeatherAlertState>(WeatherAlertState.Loading)
//    val currentWeatherAlert : StateFlow<WeatherAlertState> = _currentWeatherAlert

    private val _weatherAlerts: MutableStateFlow<LocalState> = MutableStateFlow<LocalState>(LocalState.Loading)
    val weatherAlerts : StateFlow<LocalState> = _weatherAlerts

    //Over Network
//    fun getCurrentWeatherAlert(latitude: Double, longitude: Double ) {
//        viewModelScope.launch {
//            weatherRepository.getWeatherAlerts(latitude, longitude )
//                .catch {
//                    _currentWeatherAlert.value = WeatherAlertState.Failure(it)
//
//                    Log.i(TAG, "getCurrentWeatherAlert: fail  latitude : $latitude , $latitude : $longitude")
//
//                }
//                .collect{
//                        data ->
//                    _currentWeatherAlert.value = WeatherAlertState.Success(data)
//
//                    Log.i(TAG, "getCurrentWeatherAlert: success  latitude : $latitude , $latitude : $longitude ")
//
//                }
//        }
//    }

    //From Room
    fun getAllAlertsFromRoom(){
        viewModelScope.launch{
            weatherRepository.getAllAlertsFromRoom()
                .catch {
                    _weatherAlerts.value = LocalState.Failure(it)
                }
                .collect{
                        data -> _weatherAlerts.value = LocalState.SuccessAlert(data)
                }
            Log.i(TAG, "getFavouriteCitiesFromRoom: ")
        }
    }

    fun insertToAlerts(alertDTO : AlertDTO){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.insertToAlerts(alertDTO)
            getAllAlertsFromRoom()
        }
    }
    fun removeFromAlerts(alertDTO : AlertDTO){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteFromAlerts(alertDTO)
            getAllAlertsFromRoom()
        }
    }

}