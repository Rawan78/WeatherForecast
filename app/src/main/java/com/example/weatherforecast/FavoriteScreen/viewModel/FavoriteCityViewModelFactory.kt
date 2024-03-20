package com.example.weatherforecast.FavoriteScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecast.Map.viewModel.MapViewModel
import com.example.weatherforecast.model.WeatherRepository
import java.lang.IllegalArgumentException

class FavoriteCityViewModelFactory(private val _repo: WeatherRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavoriteCityViewModel::class.java)){
            FavoriteCityViewModel(_repo) as T
        }else{
            throw IllegalArgumentException("viewmodel class not found")
        }
    }
}