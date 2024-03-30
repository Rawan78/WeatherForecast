package com.example.weatherforecast.AlertScreen.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherforecast.MainCoroutineRule
import com.example.weatherforecast.db.LocalState
import com.example.weatherforecast.getOrAwaitValue
import com.example.weatherforecast.model.FakeWeatherRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.weatherforecast.modelForAlerts.*
import kotlinx.coroutines.test.runBlockingTest


@RunWith(AndroidJUnit4::class)
class AlertScreenViewModelTest{

    lateinit var viewModel: AlertScreenViewModel
    lateinit var fakeRepo: FakeWeatherRepository

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setUp(){
        fakeRepo = FakeWeatherRepository()
        viewModel = AlertScreenViewModel(fakeRepo)
    }

    @get:Rule
    val myRule = InstantTaskExecutorRule()

    @Test
    fun insertToAlertsTest_newAlert() = runTest{
        // Given
        val alert = AlertDTO(1, "City3", 10.0, 20.0, "2024-03-28", "2024-03-29", "12:00")

        // When
        viewModel.insertToAlerts(alert)

        // Then
        fakeRepo.getAllAlertsFromRoom().collect { alerts ->
            Assert.assertTrue(alerts.contains(alert))
        }
    }

    @Test
    fun removeFromAlertsTest_oneAlert() = runTest {
        // Given
        val alert = AlertDTO(1, "City3", 10.0, 20.0, "2024-03-28", "2024-03-29", "12:00")
        fakeRepo.insertToAlerts(alert)

        // When
        viewModel.removeFromAlerts(alert)

        // Then
        fakeRepo.getAllAlertsFromRoom().collect { alerts ->
            Assert.assertFalse(alerts.contains(alert))
        }
    }

    @Test
    fun getAllAlertsFromRoomTest_twoAlerts() = runBlockingTest {
        // Given
        val alerts = mutableListOf(
            AlertDTO(2, "City4", 30.0, 40.0, "2024-03-29", "2024-03-30", "15:00"),
            AlertDTO(1, "City3", 10.0, 20.0, "2024-03-28", "2024-03-29", "12:00")
        )
        fakeRepo.fakeAlerts = alerts

        // When
        viewModel.getAllAlertsFromRoom()

        // Then
        val state = viewModel.weatherAlerts.getOrAwaitValue()
        Assert.assertEquals(LocalState.SuccessAlert(alerts), state)
    }

}