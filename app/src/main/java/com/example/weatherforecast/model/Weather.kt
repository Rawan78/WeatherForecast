package com.example.weatherforecast

import com.google.gson.annotations.SerializedName


data class Weather (

  var id          : Long,
  var main        : String,
  var description : String,
  var icon        : String

)