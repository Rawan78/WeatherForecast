package com.example.weatherforecast

import com.google.gson.annotations.SerializedName


data class Wind (

  var speed : Double,
  var deg   : Long,
   var gust  : Double

)