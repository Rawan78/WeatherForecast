package com.example.weatherforecast.model

import com.example.weatherforecast.modelForAlerts.*
import com.example.weatherforecast.network.*
import com.example.weatherforecast.db.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.lang.reflect.Constructor


class WeatherRepositoryImplTest{

    val favCity1 = FavoriteCity("City1" , 1.0 , 1.0)
    val favCity2 = FavoriteCity("City2" , 2.0 , 2.0)

    val alert1 = AlertDTO(1, "alert1", 1.0, 1.0,"2024-03-28", "2024-03-29", "12:00")
    val alert2 = AlertDTO(2, "alert2", 2.0, 2.0,"2024-03-28", "2024-03-29", "11:00")

    val favCitiesList = listOf(
        favCity1, favCity2
    )

    val alertsList = listOf(
        alert1, alert2
    )

    lateinit var fakeFavWeatherLocalDataSource: FakeWeatherLocalDataSource
    lateinit var fakeAlertsWeatherLocalDataSource: FakeWeatherLocalDataSource


    lateinit var repositoryFav: WeatherRepositoryImpl
    lateinit var repositoryAlerts: WeatherRepositoryImpl

    @Before
    fun setUp(){
        fakeFavWeatherLocalDataSource = FakeWeatherLocalDataSource()
        fakeFavWeatherLocalDataSource.fakeFavCities.value = favCitiesList.toMutableList()

        fakeAlertsWeatherLocalDataSource = FakeWeatherLocalDataSource()
        fakeAlertsWeatherLocalDataSource.fakeAlerts.value = alertsList.toMutableList()

        val constructor: Constructor<WeatherRepositoryImpl> =
            WeatherRepositoryImpl::class.java.getDeclaredConstructor(
                WeatherRemoteDataSource::class.java,
                WeatherLocalDataSource::class.java
            )
        constructor.isAccessible = true

        repositoryFav = constructor.newInstance(
            FakeWeatherRemoteDataSource(),
            fakeFavWeatherLocalDataSource
        )

        repositoryAlerts = constructor.newInstance(
            FakeWeatherRemoteDataSource(),
            fakeAlertsWeatherLocalDataSource
        )

    }

    @Test
    fun getFavCitiesFromRoomTest() = runBlockingTest {

        val favCitiesFlow = repositoryFav.getFavCitiesFromRoom()

        assertEquals(favCitiesList, favCitiesFlow.first())
    }

    @Test
    fun getAllAlertsFromRoomTest() = runBlockingTest {

        val alertsFlow = repositoryAlerts.getAllAlertsFromRoom()

        assertEquals(alertsList, alertsFlow.first())
    }

    @Test
    fun insertToFavTest() = runTest{
        val newFavoriteCity = FavoriteCity("New City", 3.0, 3.0)

        repositoryFav.insertToFav(newFavoriteCity)

        val favCitiesFlow = repositoryFav.getFavCitiesFromRoom()

        val expectedCities = favCitiesList.toMutableSet()
        expectedCities.add(newFavoriteCity)

        assertEquals(expectedCities.toList(), favCitiesFlow.first())
    }

    @Test
    fun insertToAlertsTest() = runTest{
        val newAlert = AlertDTO(1, "alert3", 3.0, 3.0,"2024-03-29", "2024-03-30", "10:00")

        repositoryAlerts.insertToAlerts(newAlert)

        val alertsFlow = repositoryAlerts.getAllAlertsFromRoom()

        val expectedAlerts = alertsList.toMutableSet()
        expectedAlerts.add(newAlert)

        assertEquals(expectedAlerts.toList(), alertsFlow.first())
    }

    @Test
    fun deleteFromFavTest() = runBlockingTest(){
        val cityToDelete = favCitiesList.first()
        repositoryFav.deleteFromFav(cityToDelete)
        val favCitiesFlow = repositoryFav.getFavCitiesFromRoom()
        assertEquals(favCitiesList - cityToDelete, favCitiesFlow.first())
    }

    @Test
    fun deleteFromAlertsTest() = runBlockingTest(){
        val alertToDelete = alertsList.first()
        repositoryAlerts.deleteFromAlerts(alertToDelete)
        val alertsFlow = repositoryAlerts.getAllAlertsFromRoom()
        assertEquals(alertsList - alertToDelete, alertsFlow.first())
    }

}