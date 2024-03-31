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
    fun addToFavTest_AndRetrieve() = runBlocking {      //pass
        // Given
        val favoriteCity = FavoriteCity("Test City1", 0.0, 0.0)

        // When
        localDataSource.addToFav(favoriteCity)

        // Then
        val favCities = localDataSource.getFavCities().first()
        assertEquals(1, favCities.size)
        assertEquals(favoriteCity, favCities[0])
    }

    @Test
    fun removeFromFavTest_oneCity() = runBlocking {   //pass
        // Given
        val favoriteCity = FavoriteCity("Test City2", 0.0, 0.0)

        localDataSource.addToFav(favoriteCity)

        var favCities = localDataSource.getFavCities().first()
        assertEquals(1, favCities.size)

        localDataSource.removeFromFav(favoriteCity)

        // Then
        favCities = localDataSource.getFavCities().first()
        assertEquals(0, favCities.size)
    }

//    @Test
//    fun addToAlerts_oneAlert() = runBlocking {
//        // Given
//        val alertDTO = AlertDTO(
//            id = 1,
//            cityName = "Test City",
//            latitude = 0.0,
//            longitude = 0.0,
//            startDate = "2024-03-28",
//            endDate = "2024-03-29",
//            time = "12:00"
//        )
//
//        // When
//        localDataSource.addToAlerts(alertDTO)
//
//        // Then
//        val storedAlerts = localDataSource.getAllAlerts().first()
//        assertEquals(1, storedAlerts.size)
//        assertEquals(alertDTO, storedAlerts[0])
//    }

    @Test
    fun addToAlertsTest_oneAlert() = runBlocking {   //pass
        // Given
        val alertDTO = AlertDTO(
            id = 1,
            cityName = "Test City3",
            latitude = 0.0,
            longitude = 0.0,
            startDate = "2024-03-28",
            endDate = "2024-03-29",
            time = "12:00"
        )

        // When
        localDataSource.addToAlerts(alertDTO)

        // Then
        val storedAlerts = localDataSource.getAllAlerts().first()
        assertEquals(1, storedAlerts.size)
        assertEquals(alertDTO, storedAlerts[0])
    }

    @Test
    fun removeFromAlertsTest_oneAlert() = runBlocking {    //pass
        // Given
        val alertDTO = AlertDTO(
            id = 1,
            cityName = "Test City4",
            latitude = 0.0,
            longitude = 0.0,
            startDate = "2024-03-28",
            endDate = "2024-03-29",
            time = "12:00"
        )

        localDataSource.addToAlerts(alertDTO)

        var storedAlerts = localDataSource.getAllAlerts().first()
        assertEquals(1, storedAlerts.size)

        localDataSource.removeFromAlerts(alertDTO)

        storedAlerts = localDataSource.getAllAlerts().first()
        assertEquals(0, storedAlerts.size)
    }


    @Test
    fun getAllFavTest_TwoCities() = runBlocking {    //pass
        // Given
        val favCity1 = FavoriteCity("City1", 10.0, 20.0)
        val favCity2 = FavoriteCity("City2", 30.0, 40.0)
        localDataSource.addToFav(favCity1)
        localDataSource.addToFav(favCity2)

        // When
        val favCities = localDataSource.getFavCities().first()

        // Then
        assertEquals(2, favCities.size)
        assertEquals(favCity1, favCities[0])
        assertEquals(favCity2, favCities[1])
    }

    @Test
    fun getAllAlertsTest_TwoAlerts() = runBlocking {   //pass
        // Given
        val alert1 = AlertDTO(1, "City3", 10.0, 20.0, "2024-03-28", "2024-03-29", "12:00")
        val alert2 = AlertDTO(2, "City4", 30.0, 40.0, "2024-03-29", "2024-03-30", "15:00")
        localDataSource.addToAlerts(alert1)
        localDataSource.addToAlerts(alert2)

        // When
        val alerts = localDataSource.getAllAlerts().first()

        // Then
        assertEquals(2, alerts.size)
        assertEquals(alert1, alerts[0])
        assertEquals(alert2, alerts[1])
    }

}