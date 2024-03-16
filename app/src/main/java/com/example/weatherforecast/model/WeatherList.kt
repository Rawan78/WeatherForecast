package com.example.weatherforecast

import com.google.gson.annotations.SerializedName


data class WeatherList (

  var dt         : Long,
  var main       : Main,
  var weather    : List<Weather>,
  var clouds     : Clouds,
  var wind       : Wind,
  var visibility : Long,
  var pop        : Double,
  var sys        : Sys,
  var dt_txt      : String

)