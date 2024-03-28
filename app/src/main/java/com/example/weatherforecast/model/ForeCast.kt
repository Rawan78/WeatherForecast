package com.example.weatherforecast

import com.google.gson.annotations.SerializedName


data class ForeCast (

  var cod     : String,
  var message : Int,
  var cnt     : Int,
  var list    : ArrayList<WeatherList>,
  var city    : City

)