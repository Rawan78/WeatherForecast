package com.example.weatherforecast.FavoriteScreen.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.model.FavoriteCity
import com.example.weatherforecast.model.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.example.weatherforecast.db.*
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch


class FavoriteCityViewModel (private val weatherRepository: WeatherRepository) : ViewModel(){
    private val _cities: MutableStateFlow<LocalState> = MutableStateFlow<LocalState>(LocalState.Loading)
    val cities : StateFlow<LocalState> = _cities

    private val TAG = "FavoriteCityViewModel"

    fun getFavouriteCitiesFromRoom(){
        viewModelScope.launch{
            weatherRepository.getFavCitiesFromRoom()
                .catch {
                    _cities.value = LocalState.Failure(it)
                }
                .collect{
                    data -> _cities.value = LocalState.Success(data)
                }
            Log.i(TAG, "getFavouriteCitiesFromRoom: ")
        }
    }

    fun insertCityToFavorite(favoriteCity : FavoriteCity){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.insertToFav(favoriteCity)
            getFavouriteCitiesFromRoom()
        }
    }
    fun removeCityFromFavorite(favoriteCity : FavoriteCity){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteFromFav(favoriteCity)
            getFavouriteCitiesFromRoom()
        }
    }
}