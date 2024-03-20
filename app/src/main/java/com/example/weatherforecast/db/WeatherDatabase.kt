package com.example.weatherforecast.db

import android.content.Context
import com.example.weatherforecast.model.*
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteCity::class], version = 1 )
abstract class WeatherDatabase : RoomDatabase(){
    abstract fun getFavoriteCityDao(): FavoriteDAO
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