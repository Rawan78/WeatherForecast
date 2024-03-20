package com.example.weatherforecast.AlertScreen.view

import android.app.AlarmManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import com.example.weatherforecast.Map.view.OpenStreetMapActivity
import com.example.weatherforecast.R

import com.example.weatherforecast.databinding.FragmentAlertsScreenBinding
import com.example.weatherforecast.databinding.FragmentFavoritesScreenBinding
import java.util.Calendar

class AlertsScreenFragment : Fragment() {

    lateinit var binding : FragmentAlertsScreenBinding
    lateinit var alarmManager: AlarmManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAlertsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAddAlarm.setOnClickListener {
            val intent = Intent(requireActivity(), OpenStreetMapActivity::class.java)
            startActivity(intent)
        }

        setSwitchButtonAlarm()

        alarmManager = 
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setSwitchButtonAlarm(){
        binding.switchAlarm.thumbTintList = getColorStateList(requireContext(),R.color.white) // White thumb when checked
        binding.switchAlarm.trackTintList = getColorStateList(requireContext(),R.color.black) // Black track when unchecked

        binding.switchAlarm.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.switchAlarm.thumbTintList = getColorStateList(requireContext(),R.color.white)
                binding.switchAlarm.trackTintList = getColorStateList( requireContext(),R.color.black)
            } else {
                binding.switchAlarm.thumbTintList = getColorStateList(requireContext(),R.color.darkGray)
                binding.switchAlarm.trackTintList = getColorStateList(requireContext(), R.color.black)
            }
        }
    }

}