package com.example.weatherforecast.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherforecast.model.FavoriteCity
import kotlinx.coroutines.flow.Flow
import com.example.weatherforecast.modelForAlerts.*

@Dao
interface AlertDAO {
    @Query("SELECT * FROM alerts_table")
    fun getStoredAlerts(): Flow<List<AlertDTO>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alertDTO: AlertDTO)

    @Delete
    suspend fun deleteAlert(alertDTO: AlertDTO)
}