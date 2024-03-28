package com.example.weatherforecast

import com.google.gson.annotations.SerializedName


data class City (

  var id         : Long,
  var name       : String,
  var coord      : Coord,
  var country    : String,
  var population : Long,
  var timezone   : Long,
  var sunrise    : Long,
  var sunset     : Long

)