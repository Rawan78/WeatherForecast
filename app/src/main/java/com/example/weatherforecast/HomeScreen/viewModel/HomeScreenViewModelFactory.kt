package com.example.weatherforecast.HomeScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.model.*
import java.lang.IllegalArgumentException


class HomeScreenViewModelFactory (private val _repo: WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(HomeScreenViewModel::class.java)){
            HomeScreenViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("viewmodel class not found")
        }
    }
}