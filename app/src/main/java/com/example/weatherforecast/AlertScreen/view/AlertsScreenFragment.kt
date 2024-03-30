package com.example.weatherforecast.AlertScreen.view

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.AlertScreen.viewModel.*
import com.example.weatherforecast.HomeScreen.viewModel.HomeScreenViewModel
import com.example.weatherforecast.HomeScreen.viewModel.HomeScreenViewModelFactory
import com.example.weatherforecast.R

import com.example.weatherforecast.databinding.FragmentAlertsScreenBinding
import com.example.weatherforecast.db.WeatherLocalDataSourceImpl
import com.example.weatherforecast.model.WeatherRepositoryImpl
import com.example.weatherforecast.network.WeatherRemoteDataSourceImpl
import com.example.weatherforecast.db.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.example.weatherforecast.SharedPrefs


import com.example.weatherforecast.network.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

import com.example.weatherforecast.modelForAlerts.*


class AlertsScreenFragment : Fragment()  , OnAlertClickListener {

    private val TAG = "AlertsScreenFragment"

    lateinit var binding : FragmentAlertsScreenBinding

    private lateinit var alertScreenViewModel: AlertScreenViewModel
    private lateinit var alertScreenViewModelFactory: AlertScreenViewModelFactory
    private lateinit var alertsAdapter: AlertsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerViewAlerts: RecyclerView

    private lateinit var homeScreenViewModelFactory: HomeScreenViewModelFactory
    private lateinit var homeScreenViewModel: HomeScreenViewModel


    private lateinit var sharedPrefs: SharedPrefs

    private var isContextAttached = false

     var alertDescription: String? = null
     var countryName: String? = null

//    val latitudeForAlert: Double = sharedPrefs.getLatitudeForAlert()
//    val longitudeForAlert: Double = sharedPrefs.getLongitudeForAlert()
//    val currentLanguage = sharedPrefs.getLanguage()
//    val currentUnit = sharedPrefs.getTemp()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isContextAttached = true
        sharedPrefs = SharedPrefs.getInstance(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAlertsScreenBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isContextAttached) {
            return
        }

        recyclerViewAlerts = binding.rvAlerts
        recyclerViewAlerts.layoutManager = LinearLayoutManager(requireContext())

        alertScreenViewModelFactory = AlertScreenViewModelFactory(
            WeatherRepositoryImpl.getInstance
            (WeatherRemoteDataSourceImpl.getInstance() ,  WeatherLocalDataSourceImpl(requireContext())))

        alertScreenViewModel = ViewModelProvider(this, alertScreenViewModelFactory).get(
            AlertScreenViewModel::class.java)

        homeScreenViewModelFactory = HomeScreenViewModelFactory(WeatherRepositoryImpl.getInstance
            (WeatherRemoteDataSourceImpl.getInstance() ,  WeatherLocalDataSourceImpl(requireContext())))

        homeScreenViewModel = ViewModelProvider(this, homeScreenViewModelFactory).get(HomeScreenViewModel::class.java)


        lifecycleScope.launch{
            homeScreenViewModel.currentWeather.collectLatest{result ->
                when (result){
                    is WeatherState.Loading -> {
                        //recyclerViewFavoriteCities.visibility = View.GONE
                        Log.i(TAG, "onCreate: loading")
                    }
                    is WeatherState.Success -> {
                       // updateUI(result.weatherResponse)
                        alertDescription = result.weatherResponse.list[0].weather.firstOrNull()?.description ?: ""
                        countryName = result.weatherResponse.city.country

                    }
                    else -> {
                        //Toast.makeText(requireContext(), "Failed to fetch cities", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

       homeScreenViewModel.getCurrentWeather(
            sharedPrefs.getLatitudeForAlert(),
            sharedPrefs.getLongitudeForAlert(),
            sharedPrefs.getTemp().toString(),
            sharedPrefs.getLanguage().toString()
            )



        setUpRecyclerView()

        lifecycleScope.launch{
            alertScreenViewModel.weatherAlerts.collectLatest{result ->
                when (result){
                    is LocalState.Loading -> {
                        binding.progressBar2.visibility = View.VISIBLE
                        recyclerViewAlerts.visibility = View.GONE
                    }
                    is LocalState.SuccessAlert -> {
                        binding.progressBar2.visibility = View.GONE
                        recyclerViewAlerts.visibility = View.VISIBLE
                        alertsAdapter.setAlerts(result.alertDTO)
                    }
                    else -> {
                        binding.progressBar2.visibility = View.GONE
                        Toast.makeText(requireContext(), "Failed to fetch alerts", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        alertScreenViewModel.getAllAlertsFromRoom()
        

        binding.fabAddAlarm.setOnClickListener {
//            val intent = Intent(requireActivity(), OpenStreetMapActivity::class.java)
//            startActivity(intent)
            showAlertDialog()
        }

        val lastSelectedOption = sharedPrefs.getAlarmType()

        // Set the default option to alarm type if no option is previously selected
        if (lastSelectedOption.isNullOrEmpty()) {
            binding.radioNotification.isChecked = true
        } else {
            // Set the last selected option
            when (lastSelectedOption) {
                "default_alarm_sound" -> binding.radioDefaultAlarmSound.isChecked = true
                "notification" -> binding.radioNotification.isChecked = true
            }
        }

        binding.radioGroupAlert.setOnCheckedChangeListener{ _, checkedId ->
            when (checkedId) {
                R.id.radio_default_alarm_sound -> {
                    sharedPrefs.setAlamType("default_alarm_sound")
                }
                R.id.radio_notification -> {
                    sharedPrefs.setAlamType("notification")
                }
            }
        }

        setSwitchButtonAlarm()
    }

    private fun setUpRecyclerView(){
        linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        alertsAdapter = AlertsAdapter(requireContext() , ArrayList() , this)

        binding.rvAlerts.apply {
            adapter = alertsAdapter
            layoutManager = linearLayoutManager
        }
        Log.i(TAG, "setUpRecyclerView: ")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setSwitchButtonAlarm(){
        // Load the last saved alarm state
        val lastAlarmState = sharedPrefs.getAlarmState()?.toBoolean()

        // Set the switch button state
        binding.switchAlarm.isChecked = lastAlarmState ?: false

        // Set the switch button colors based on its state
        if (binding.switchAlarm.isChecked) {
            binding.switchAlarm.thumbTintList = getColorStateList(requireContext(), R.color.white)
            binding.switchAlarm.trackTintList = getColorStateList(requireContext(), R.color.black)
        } else {
            binding.switchAlarm.thumbTintList = getColorStateList(requireContext(), R.color.darkGray)
            binding.switchAlarm.trackTintList = getColorStateList(requireContext(), R.color.black)
        }

        binding.switchAlarm.setOnCheckedChangeListener { buttonView, isChecked ->
            // Set the switch button colors based on its state
            if (isChecked) {
                binding.switchAlarm.thumbTintList = getColorStateList(requireContext(), R.color.white)
                binding.switchAlarm.trackTintList = getColorStateList(requireContext(), R.color.black)
            } else {
                binding.switchAlarm.thumbTintList = getColorStateList(requireContext(), R.color.darkGray)
                binding.switchAlarm.trackTintList = getColorStateList(requireContext(), R.color.black)
            }

            // Save the alarm state to SharedPreferences
            sharedPrefs.setAlarmState(isChecked.toString())
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun showAlertDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog_alert_layout, null)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Set Alert")
            //.setMessage("Message")

        val alertDialog = builder.create()
        // Set click listeners for buttons in the custom dialog layout
        dialogView.findViewById<Button>(R.id.btnStartDate)?.setOnClickListener {
            showDatePickerDialog(requireContext(), alertDialog , dialogView.findViewById<TextView>(R.id.tv_start_date))
        }

        dialogView.findViewById<Button>(R.id.btnEndDate)?.setOnClickListener {
            showDatePickerDialog(requireContext(), alertDialog , dialogView.findViewById<TextView>(R.id.tv_end_date))
        }

        dialogView.findViewById<Button>(R.id.btnPickTime)?.setOnClickListener {
            showTimePickerDialog(requireContext(), alertDialog , dialogView.findViewById<TextView>(R.id.tv_time_for_alert))
        }

        dialogView.findViewById<Button>(R.id.btnChooseLocation)?.setOnClickListener {
            // Retrieve latitude and longitude from SharedPreferences and toast them

            val latitude = sharedPrefs.getLatitudeForAlert()
            val longitude = sharedPrefs.getLongitudeForAlert()
            val cityName = sharedPrefs.getCityNameForAlert()
            if (latitude != 0.0 && longitude != 0.0) {

                dialogView.findViewById<TextView>(R.id.tv_location_for_alert).text = cityName

                Toast.makeText(requireContext(), " City: $cityName", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "City not found", Toast.LENGTH_SHORT).show()
            }
            // Don't dismiss the dialog after launching the activity
        }

        dialogView.findViewById<Button>(R.id.btnSave)?.setOnClickListener {

            clearNotifications()

            // Retrieve the values from the views
            val start_date = dialogView.findViewById<TextView>(R.id.tv_start_date).text.toString()
            val end_date = dialogView.findViewById<TextView>(R.id.tv_end_date).text.toString()
            val timeForAlert = dialogView.findViewById<TextView>(R.id.tv_time_for_alert).text.toString()
            val city_name = dialogView.findViewById<TextView>(R.id.tv_location_for_alert).text.toString()

            if (start_date.isNullOrEmpty() || end_date.isNullOrEmpty() || timeForAlert.isNullOrEmpty() || city_name.isNullOrEmpty()) {
                // Show Toast to notify the user to fill all fields
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {

                val alert = AlertDTO(
                    id = 0,
                    cityName = city_name,
                    latitude = sharedPrefs.getLatitudeForAlert(),
                    longitude = sharedPrefs.getLongitudeForAlert(),
                    startDate = start_date,
                    endDate = end_date,
                    time = timeForAlert
                )

                // Check if the alarm is on before scheduling
                if (sharedPrefs.getAlarmState()?.toBoolean() == true) {
                    if (sharedPrefs.getAlarmType()=="default_alarm_sound") {
                        scheduleMyAlarm(alert)
                    } else {
                        scheduleAlarmForAlert(alert)
                    }
                }

                alertScreenViewModel.insertToAlerts(alert)
                alertDialog.dismiss()
            }
        }

        dialogView.findViewById<Button>(R.id.btnCancel)?.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun showDatePickerDialog(context: Context, alertDialog: AlertDialog , textView: TextView) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                // Handle the selected start date here

                textView.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }


    private fun showTimePickerDialog(context: Context, alertDialog: AlertDialog , textView: TextView) {
        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                // Handle the selected time here
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                textView.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedTime.time)

                //Toast.makeText(context, "Selected Time: ${selectedTime.time}", Toast.LENGTH_SHORT).show()
                // Don't dismiss the dialog
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // 24-hour format
        )
        timePicker.show()
    }

    override fun onAlertClick(alertDTO: AlertDTO) {
        alertScreenViewModel.removeFromAlerts(alertDTO)
        Toast.makeText(requireContext(), "Alert for ${alertDTO.cityName} deleted", Toast.LENGTH_SHORT).show()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERM) {
            if (grantResults.size == 1) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                        //startForegroundService(intent)
                    }
            }
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleAlarmForAlert(alertDTO: AlertDTO) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlertReceiver::class.java).apply {
            putExtra("cityName", alertDTO.cityName)
            //putExtra("country", countryName)
            putExtra("alert_description" , alertDescription)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alertDTO.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate the alarm time
        val alarmTime = calculateAlarmTime(alertDTO.startDate, alertDTO.time)
        Log.i(TAG, "scheduleAlarmForAlert: alarmTime -> $alarmTime")

        // Use setExactAndAllowWhileIdle() for precise timing
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
    }

    @SuppressLint("SimpleDateFormat")
    private fun calculateAlarmTime(startDate: String, time: String): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateTime = "$startDate $time"
        val date = dateFormat.parse(dateTime)

        return date?.time ?: 0L
    }

    private fun clearNotifications() {
        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.cancelAll()
    }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun scheduleMyAlarm(alertDTO: AlertDTO) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("cityName", alertDTO.cityName)
            //putExtra("country", countryName)
            putExtra("alert_description" , alertDescription)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            alertDTO.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate the alarm time
        val alarmTime = calculateAlarmTime(alertDTO.startDate, alertDTO.time)

        // Use setExactAndAllowWhileIdle() for precise timing
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
    }

}