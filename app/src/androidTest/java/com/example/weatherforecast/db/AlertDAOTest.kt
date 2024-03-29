package com.example.weatherforecast.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.weatherforecast.modelForAlerts.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals


@SmallTest
@RunWith(AndroidJUnit4::class)
class AlertDAOTes {
    lateinit var database: WeatherDatabase
    lateinit var dao : AlertDAO

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        database =  Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()

        dao = database.getAlertsDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun insertAlertAndGetStoredAlerts() = runBlocking {
        val alert = AlertDTO(
            id = 1,
            cityName = "City",
            latitude = 0.0,
            longitude = 0.0,
            startDate = "2024-03-28",
            endDate = "2024-03-29",
            time = "12:00"
        )
        dao.insertAlert(alert)

        val storedAlerts = dao.getStoredAlerts().first()
        assertEquals(1, storedAlerts.size)
        assertEquals(alert, storedAlerts[0])
    }

    @Test
    fun deleteAlert_deleteOneAlert() = runBlocking {
        val alert = AlertDTO(
            id = 1,
            cityName = "City",
            latitude = 0.0,
            longitude = 0.0,
            startDate = "2024-03-28",
            endDate = "2024-03-29",
            time = "12:00"
        )
        dao.insertAlert(alert)

        dao.deleteAlert(alert)

        val storedAlerts = dao.getStoredAlerts().first()
        assertEquals(0, storedAlerts.size)
    }


}