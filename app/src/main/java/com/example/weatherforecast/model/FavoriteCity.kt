package com.example.weatherforecast.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "favorite_table")
data class FavoriteCity(@PrimaryKey
                        var name:String,
                        var lat : Double,
                        var lon : Double) : Serializable
