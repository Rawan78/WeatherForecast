package com.example.weatherforecast.modelForAlerts

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "alerts_table")
data class AlertDTO( @PrimaryKey(autoGenerate = true)
                    var id : Int,
                    var cityName : String,
                    var latitude : Double,
                    var longitude : Double,
                    var startDate : String,
                    var endDate : String,
                    var time : String ) : Serializable
