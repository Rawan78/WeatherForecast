package com.example.weatherforecast.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFavorites(favoriteCities: List<FavoriteCity>)

    @Query("SELECT * FROM favorite_table")
     fun getStoredFavoriteCities(): Flow<List<FavoriteCity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteCity: FavoriteCity)

    @Delete
    suspend fun deleteFavorite(favoriteCity: FavoriteCity)
}