package com.example.weatherforecast.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.weatherforecast.model.*
import com.example.weatherforecast.modelForAlerts.*



@MediumTest
class WeatherLocalDataSourceImplTest{
    lateinit var database: WeatherDatabase
    lateinit var localDataSource: WeatherLocalDataSourceImpl

    @get:Rule
    val rule = InstantTaskExecutorRule()    //force that all tasks work in the same thread

    @Before
    fun setUp(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries()
            .build()

        localDataSource = WeatherLocalDataSourceImpl(ApplicationProvider.getApplicationContext())

    }

    @After
    fun teatDown(){
        database.close()
    }

    @Test
    fun testGetFavCities() = runBlocking {
        val favoriteCities = listOf(
            FavoriteCity("City1", 1.0, 1.0),
            FavoriteCity("City2", 2.0, 2.0)
        )
        database.getFavoriteCityDao().insertAllFavorites(favoriteCities)

        val storedFavorites = localDataSource.getFavCities().first()
        assertEquals(favoriteCities, storedFavorites)
    }

    @Test
    fun testAddToFav() = runBlocking {
        val favoriteCity = FavoriteCity("City1", 1.0, 1.0)
        localDataSource.addToFav(favoriteCity)

        val storedFavorites = database.getFavoriteCityDao().getStoredFavoriteCities().first()
        assertEquals(listOf(favoriteCity), storedFavorites)
    }

    @Test
    fun testAddToAlerts() = runBlocking {
        val alertDTO = AlertDTO(
            1,
            "City1",
            1.0,
            1.0,
            "2024-03-28",
            "2024-03-30",
            "08:00"
        )
        localDataSource.addToAlerts(alertDTO)

        val storedAlerts = database.getAlertsDao().getStoredAlerts().first()
        assertEquals(listOf(alertDTO), storedAlerts)
    }

    @Test
    fun testRemoveFromAlerts() = runBlocking {
        val alertDTO = AlertDTO(
            1,
            "City1",
            1.0,
            1.0,
            "2024-03-28",
            "2024-03-30",
            "08:00"
        )
        database.getAlertsDao().insertAlert(alertDTO)

        // Verify that the alert is initially present in the database
        var storedAlerts = database.getAlertsDao().getStoredAlerts().first()
        assertEquals(listOf(alertDTO), storedAlerts)

        // Remove the alert
        localDataSource.removeFromAlerts(alertDTO)

        // Verify that the alert is removed from the database
        storedAlerts = database.getAlertsDao().getStoredAlerts().first()
        assertEquals(emptyList<AlertDTO>(), storedAlerts)
    }
}