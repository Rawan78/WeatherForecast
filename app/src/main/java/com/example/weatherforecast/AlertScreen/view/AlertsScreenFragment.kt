package com.example.weatherforecast.AlertScreen.view

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherforecast.AlertScreen.viewModel.*
import com.example.weatherforecast.HomeScreen.viewModel.HomeScreenViewModel
import com.example.weatherforecast.HomeScreen.viewModel.HomeScreenViewModelFactory
import com.example.weatherforecast.Map.view.OpenStreetMapActivity
import com.example.weatherforecast.R

import com.example.weatherforecast.databinding.FragmentAlertsScreenBinding
import com.example.weatherforecast.databinding.FragmentFavoritesScreenBinding
import com.example.weatherforecast.db.WeatherLocalDataSourceImpl
import com.example.weatherforecast.model.WeatherRepositoryImpl
import com.example.weatherforecast.network.WeatherRemoteDataSourceImpl
import com.example.weatherforecast.network.WeatherState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import com.example.weatherforecast.network.*
import java.util.Calendar


class AlertsScreenFragment : Fragment() {

    private val TAG = "AlertsScreenFragment"

    lateinit var binding : FragmentAlertsScreenBinding

//    private lateinit var alertScreenViewModel: AlertScreenViewModel
//    private lateinit var alertScreenViewModelFactory: AlertScreenViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAlertsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        alertScreenViewModelFactory = AlertScreenViewModelFactory(
//            WeatherRepositoryImpl.getInstance
//            (WeatherRemoteDataSourceImpl.getInstance() ,  WeatherLocalDataSourceImpl(requireContext())))
//
//        alertScreenViewModel = ViewModelProvider(this, alertScreenViewModelFactory).get(
//            AlertScreenViewModel::class.java)

//        lifecycleScope.launch{
//            alertScreenViewModel.currentWeatherAlert.collectLatest{result ->
//                when (result){
//                    is WeatherAlertState.Loading -> {
//
//                        Log.i(TAG, "onCreate: loading")
//                    }
//                    is WeatherAlertState.Success -> {
//
//                        Log.i(TAG, "onViewCreated: alerts :  ${result.weatherAlertResponse.alerts}")
//
//                    }
//                    else -> {
//                        Toast.makeText(requireContext(), "Failed to fetch cities", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        }
        

        binding.fabAddAlarm.setOnClickListener {
//            val intent = Intent(requireActivity(), OpenStreetMapActivity::class.java)
//            startActivity(intent)

            showAlertDialog()
            
            

        }

        setSwitchButtonAlarm()

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

    private fun showAlertDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog_alert_layout, null)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Title")
            .setMessage("Message")

        val alertDialog = builder.create()

        // Set click listeners for buttons in the custom dialog layout
        dialogView.findViewById<Button>(R.id.btnStartDate)?.setOnClickListener {
            showDatePickerDialog(requireContext(), alertDialog)
        }

        dialogView.findViewById<Button>(R.id.btnEndDate)?.setOnClickListener {
            showDatePickerDialog(requireContext(), alertDialog)
        }

        dialogView.findViewById<Button>(R.id.btnPickTime)?.setOnClickListener {
            showTimePickerDialog(requireContext(), alertDialog)
        }

        dialogView.findViewById<Button>(R.id.btnChooseLocation)?.setOnClickListener {
            val intent = Intent(requireActivity(), OpenStreetMapActivity::class.java)
            startActivity(intent)
            // Don't dismiss the dialog after launching the activity
        }

        alertDialog.show()
    }

    private fun showDatePickerDialog(context: Context, alertDialog: AlertDialog) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                // Handle the selected start date here
                Toast.makeText(context, "Selected Date: ${selectedDate.time} , ${selectedDate.firstDayOfWeek}", Toast.LENGTH_SHORT).show()
                // Don't dismiss the dialog
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }


    private fun showTimePickerDialog(context: Context, alertDialog: AlertDialog) {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                // Handle the selected time here
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                Toast.makeText(context, "Selected Time: ${selectedTime.time}", Toast.LENGTH_SHORT).show()
                // Don't dismiss the dialog
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // 24-hour format
        )
        timePicker.show()
    }



    private fun showStartDatePickerDialog(context: Context) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                // Handle the selected start date here
                Toast.makeText(context, "Selected Start Date: ${selectedDate.time} , ${selectedDate.firstDayOfWeek}", Toast.LENGTH_SHORT).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun showTimePickerDialog(context: Context) {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                // Handle the selected time here
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                Toast.makeText(context, "Selected Time: ${selectedTime.time}", Toast.LENGTH_SHORT).show()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // 24-hour format
        )
        timePicker.show()
    }


}