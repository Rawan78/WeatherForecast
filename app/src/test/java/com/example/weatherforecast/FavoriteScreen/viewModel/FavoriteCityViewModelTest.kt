package com.example.weatherforecast.FavoriteScreen.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import com.example.weatherforecast.*
import org.junit.runner.RunWith
import com.example.weatherforecast.model.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import com.example.weatherforecast.db.LocalState
import kotlinx.coroutines.ExperimentalCoroutinesApi

import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue


@RunWith(AndroidJUnit4::class)
class FavoriteCityViewModelTest{

    lateinit var viewModel: FavoriteCityViewModel
    lateinit var fakeRepo: FakeWeatherRepository

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp(){
        fakeRepo = FakeWeatherRepository()
        viewModel = FavoriteCityViewModel(fakeRepo)
    }

    @get:Rule
    val myRule = InstantTaskExecutorRule()

    @Test
    fun insertCityToFavorite_newCity() = runTest{
        // Given
        val city = FavoriteCity("New York", 40.7128, -74.0060)

        // When
        viewModel.insertCityToFavorite(city)

        // Then
        fakeRepo.getFavCitiesFromRoom().collect { cities ->
            assertTrue(cities.contains(city))
        }
    }

    @Test
    @ExperimentalCoroutinesApi
    fun removeCityFromFavoriteTest() = runBlockingTest {
        // Given
        val city = FavoriteCity("New York", 40.7128, -74.0060)
        fakeRepo.insertToFav(city)

        // When
        viewModel.removeCityFromFavorite(city)

        // Then
        fakeRepo.getFavCitiesFromRoom().collect { cities ->
            assertFalse(cities.contains(city))
        }
    }

    @Test
    fun getFavouriteCitiesFromRoomTest() = runBlockingTest {
        // Given
        val cities = mutableListOf(
            FavoriteCity("City 1", 1.0, 1.0),
            FavoriteCity("City 2", 2.0, 2.0)
        )
        fakeRepo.fakeFavCities = cities

        // When
        viewModel.getFavouriteCitiesFromRoom()

        // Then
        val state = viewModel.cities.getOrAwaitValue()
        assertEquals(LocalState.Success(cities), state)

    }
}