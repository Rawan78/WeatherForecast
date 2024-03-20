package com.example.weatherforecast.Map.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MapViewModel (private val weatherRepository: WeatherRepository) : ViewModel(){
//    private val _cities: MutableLiveData<List<FavoriteCity>> = MutableLiveData<List<FavoriteCity>>()
//    val products : LiveData<List<FavoriteCity>> = _cities

    fun addCityToFavorite(favoriteCity : FavoriteCity){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.insertToFav(favoriteCity)
        }
    }
}