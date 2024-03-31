package com.example.weatherforecast.db

import android.content.Context
import com.example.weatherforecast.model.*
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherforecast.modelForAlerts.*
import com.example.weatherforecast.*

@Database(entities = [FavoriteCity::class , AlertDTO::class , WeatherResponse::class], version = 3 )
@TypeConverters(WeatherListConverter::class, CityConverter::class)
abstract class WeatherDatabase : RoomDatabase(){
    abstract fun getFavoriteCityDao(): FavoriteDAO
    abstract fun getAlertsDao(): AlertDAO
    abstract fun getWeatherDao(): CurrentWeatherDAO
    companion object{
        @Volatile
        private var INSTANCE: WeatherDatabase? = null
        fun getInstance (ctx: Context): WeatherDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext, WeatherDatabase::class.java, "weather_database")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance }
        }
    }
}