package com.example.weatherforecast.Settings.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.recreate
import com.example.weatherforecast.R
import com.example.weatherforecast.databinding.FragmentSettingsScreenBinding
import com.example.weatherforecast.Map.view.*
import com.example.weatherforecast.SharedPrefs



class SettingsScreenFragment : Fragment() {
    private lateinit var binding: FragmentSettingsScreenBinding

    private lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefs = SharedPrefs.getInstance(requireContext())

        binding.apply {
            val windSpeedPreference = sharedPrefs.getWindSpeedPreference()

            // Check the appropriate radio button based on the wind speed preference
            when (windSpeedPreference) {
                "Miles/Hour" -> radioMilHour.isChecked = true
                "Meter/Sec" -> radioMeterSec.isChecked = true
                else -> radioMeterSec.isChecked = true // Default to "Meter/Sec" if the preference is not set
            }

            // Set the radio button listener
            radioGroupWind.setOnCheckedChangeListener { group, checkedId ->
                val selectedOption = when (checkedId) {
                    R.id.radio_mil_hour -> "Miles/Hour"
                    R.id.radio_meter_sec -> "Meter/Sec"
                    else -> "Meter/Sec"
                }
                selectedOption?.let {
                    sharedPrefs.clearWindSpeedPreference()
                    sharedPrefs.setWindSpeedPreference(it)
                    Toast.makeText(context, "$it selected", Toast.LENGTH_SHORT).show()
                }
            }

            // Temperature Settings
            val temperaturePreference = sharedPrefs.getTemp()
            when (temperaturePreference) {
                "Kelvin" -> radioKelvin.isChecked = true
                "Celsius" -> radioCelsius.isChecked = true
                "Fahrenheit" -> radioFahrenheit.isChecked = true
            }
            radioGroupTemperature.setOnCheckedChangeListener { group, checkedId ->
                val selectedOption = when (checkedId) {
                    R.id.radio_kelvin -> "Kelvin"
                    R.id.radio_celsius -> "Celsius"
                    R.id.radio_fahrenheit -> "Fahrenheit"
                    else -> "Celsius"
                }
                selectedOption?.let {
                    sharedPrefs.setTemp(it)
                    Toast.makeText(context, "$it selected", Toast.LENGTH_SHORT).show()
                }
            }

            val selectedLanguage = sharedPrefs.getLanguage()
            when (selectedLanguage) {
                "ar" -> radioArabic.isChecked = true
                "en" -> radioEnglish.isChecked = true
            }
            radioGroupLanguage.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.radio_arabic -> {
                        sharedPrefs.setLanguage("ar")
                        Toast.makeText(context, "Arabic selected", Toast.LENGTH_SHORT).show()
                    }
                    R.id.radio_english -> {
                        sharedPrefs.setLanguage("en")
                        Toast.makeText(context, "English selected", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Location Settings
            val locationPreference = sharedPrefs.getLocationMode()
            when (locationPreference) {
                LOCATION_GPS -> radioGPS.isChecked = true
                LOCATION_MAP -> {
                    radioMap.isChecked = true
                    val intent = Intent(activity, OpenStreetMapActivity::class.java)
                    startActivity(intent)
                }
                else -> radioGPS.isChecked = true // Default to GPS
            }

            radioGroupLocation.setOnCheckedChangeListener { _, checkedId ->
                val selectedLocation = when (checkedId) {
                    R.id.radio_GPS -> LOCATION_GPS
                    R.id.radio_map -> LOCATION_MAP
                    else -> LOCATION_GPS // Default to GPS
                }
                selectedLocation?.let {
                    sharedPrefs.setLocationMode(it)
                    Toast.makeText(context, "$it selected", Toast.LENGTH_SHORT).show()
                    if (it == LOCATION_MAP) {
                        val intent = Intent(activity, OpenStreetMapActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

        }

    }
    companion object {
        private const val LOCATION_GPS = "GPS"
        private const val LOCATION_MAP = "Map"
    }

}