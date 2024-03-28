package com.example.weatherforecast.AlertScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.FavoriteScreen.viewModel.FavoriteCityViewModel
import com.example.weatherforecast.model.WeatherRepository
import java.lang.IllegalArgumentException

class AlertScreenViewModelFactory (private val _repo: WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(AlertScreenViewModel::class.java)){
            AlertScreenViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("viewmodel class not found")
        }
    }
}