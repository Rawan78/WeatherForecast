package com.example.weatherforecast.HomeScreen.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.FavoriteScreen.viewModel.FavoriteCityViewModel
import com.example.weatherforecast.MainCoroutineRule
import com.example.weatherforecast.model.FakeWeatherRepository
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.weatherforecast.model.*

@RunWith(AndroidJUnit4::class)
class HomeScreenViewModelTest{
    lateinit var viewModel: HomeScreenViewModel
    lateinit var fakeRepo: FakeWeatherRepository

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp(){
        fakeRepo = FakeWeatherRepository()
        viewModel = HomeScreenViewModel(fakeRepo)
    }

    @get:Rule
    val myRule = InstantTaskExecutorRule()

}