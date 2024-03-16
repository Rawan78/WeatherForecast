package com.example.weatherforecast

import com.google.gson.annotations.SerializedName


data class Main (

  var temp      : Double,
  var feelsLike : Double,
  var temp_min   : Double,
  var temp_max   : Double,
  var pressure  : Long,
  var seaLevel  : Long,
  var grndLevel : Long,
  var humidity  : Long,
  var tempKf    : Double

)