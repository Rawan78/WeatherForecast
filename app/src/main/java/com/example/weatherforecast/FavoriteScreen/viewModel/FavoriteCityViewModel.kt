package com.example.weatherforecast.FavoriteScreen.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.flow.collect


class FavoriteCityViewModel (private val weatherRepository: WeatherRepository) : ViewModel(){
    private val _cities: MutableStateFlow<FavoriteCityState> = MutableStateFlow<FavoriteCityState>(FavoriteCityState.Loading)
    val cities : StateFlow<FavoriteCityState> = _cities

    private val TAG = "FavoriteCityViewModel"

    fun getFavouriteCitiesFromRoom(){
        viewModelScope.launch{
            weatherRepository.getFavCitiesFromRoom()
                .catch {
                    _cities.value = FavoriteCityState.Failure(it)
                }
                .collect{
                    data -> _cities.value = FavoriteCityState.Success(data)
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