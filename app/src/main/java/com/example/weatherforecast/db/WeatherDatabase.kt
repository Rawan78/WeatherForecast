package com.example.weatherforecast.db

import android.content.Context
import com.example.weatherforecast.model.*
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherforecast.modelForAlerts.*

@Database(entities = [FavoriteCity::class , AlertDTO::class], version = 2 )
abstract class WeatherDatabase : RoomDatabase(){
    abstract fun getFavoriteCityDao(): FavoriteDAO
    abstract fun getAlertsDao(): AlertDAO
    companion object{
        @Volatile
        private var INSTANCE: WeatherDatabase? = null
        fun getInstance (ctx: Context): WeatherDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, WeatherDatabase::class.java, "weather_database")
                    .build()
                INSTANCE = instance
                instance }
        }
    }
}