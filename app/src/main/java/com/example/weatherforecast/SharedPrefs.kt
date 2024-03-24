package com.example.weatherforecast

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SharedPrefs internal constructor(private val context: Context){

    companion object{
        private const val SHARED_PREFS_NAME = "my_prefs"
        private const val KEY_CITY = "city"

        private const val KEY_WIND_SPEED = "wind_speed"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_TEMP = "temp"
        private const val KEY_LOCATION_MODE = "location_mode"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"


        @SuppressLint("StaticFieldLeak")
        private var instance : SharedPrefs? = null

        fun getInstance(context: Context): SharedPrefs {
            return instance ?: synchronized(this) {
                instance ?: SharedPrefs(context.applicationContext).also { instance = it }
            }
        }

    }

    private val prefs : SharedPreferences by lazy {
        context.getSharedPreferences(SHARED_PREFS_NAME , Context.MODE_PRIVATE)
    }


    //For Wind Speed
    fun setWindSpeedPreference(speed: String) {
        prefs.edit().putString(KEY_WIND_SPEED, speed).apply()
    }

    fun getWindSpeedPreference(): String? {
        return prefs.getString(KEY_WIND_SPEED, "Meter/Sec")
    }

    fun clearWindSpeedPreference() {
        prefs.edit().remove(KEY_WIND_SPEED).apply()
    }

    //For Language
    fun setLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }

    fun getLanguage(): String? {
        return prefs.getString(KEY_LANGUAGE, "en")
    }

    fun clearLanguage() {
        prefs.edit().remove(KEY_LANGUAGE).apply()
    }


    //For Temperature
    fun setTemp(temp : String) {
        prefs.edit().putString(KEY_TEMP, temp).apply()
    }

    fun getTemp(): String? {
        return prefs.getString(KEY_TEMP, "")
    }

    fun clearTemp() {
        prefs.edit().remove(KEY_LANGUAGE).apply()
    }

    // For location mode
    fun setLocationMode(mode: String) {
        prefs.edit().putString(KEY_LOCATION_MODE, mode).apply()
    }

    fun getLocationMode(): String? {
        return prefs.getString(KEY_LOCATION_MODE, "")
    }

    fun clearLocationMode() {
        prefs.edit().remove(KEY_LOCATION_MODE).apply()
    }

    // For latitude
    fun setLatitude(latitude: Double) {
        prefs.edit().putFloat(KEY_LATITUDE, latitude.toFloat()).apply()
    }

    fun getLatitude(): Double {
        return prefs.getFloat(KEY_LATITUDE, 0.0f).toDouble()
    }

    // For longitude
    fun setLongitude(longitude: Double) {
        prefs.edit().putFloat(KEY_LONGITUDE, longitude.toFloat()).apply()
    }

    fun getLongitude(): Double {
        return prefs.getFloat(KEY_LONGITUDE, 0.0f).toDouble()
    }


    fun setValue(key : String , value :String){
        prefs.edit().putString(key , value)
    }

    fun getValue(key : String ) : String?{
        return prefs.getString(key , null)
    }

    fun setValueOrNull(key : String? , value :String?){

        if (key != null && value != null){
            prefs.edit().putString(key , value).apply()
        }
    }

    fun getValueOrNull(key : String? ):String?{

        if (key != null){
            prefs.edit().putString(key , null)
        }

        return null
    }

    fun clearCityValue(){
        prefs.edit().remove(KEY_CITY).apply()
    }

}