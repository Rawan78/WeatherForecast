package com.example.weatherforecast.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import com.example.weatherforecast.model.*
import kotlinx.coroutines.flow.first


@SmallTest
@RunWith(AndroidJUnit4::class)
class FavoriteDAOTest {
    lateinit var database: WeatherDatabase
    lateinit var dao : FavoriteDAO

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        database =  Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()

        dao = database.getFavoriteCityDao()
    }

    @After
    fun tearDown(){
        database.close()
    }

    @Test
    fun testInsertAllFavorites_insert2Cities() = runBlocking {
        val favoriteCities = listOf(
            FavoriteCity("City1", 1.0, 1.0),
            FavoriteCity("City2", 2.0, 2.0)
        )
        dao.insertAllFavorites(favoriteCities)

        val storedFavorites = dao.getStoredFavoriteCities().first()
        assertEquals(favoriteCities, storedFavorites)
    }

    @Test
    fun testGetStoredFavoriteCities_retrieveFavoriteCities() = runBlocking {
        val favoriteCities = listOf(
            FavoriteCity("City1", 1.0, 1.0),
            FavoriteCity("City2", 2.0, 2.0)
        )
        dao.insertAllFavorites(favoriteCities)

        val storedFavorites = dao.getStoredFavoriteCities().first()
        assertEquals(favoriteCities, storedFavorites)
    }

    @Test
    fun testInsertFavorite_oneFavoriteCity() = runBlocking {
        val favoriteCity = FavoriteCity("City1", 1.0, 1.0)
        dao.insertFavorite(favoriteCity)

        val storedFavorites = dao.getStoredFavoriteCities().first()
        assertEquals(listOf(favoriteCity), storedFavorites)
    }

    @Test
    fun testDeleteFavorite_oneFavoriteCity() = runBlocking {
        val favoriteCity = FavoriteCity("City1", 1.0, 1.0)
        dao.insertFavorite(favoriteCity)

        dao.deleteFavorite(favoriteCity)

        val storedFavorites = dao.getStoredFavoriteCities().first()
        assertEquals(emptyList<FavoriteCity>(), storedFavorites)
    }

}