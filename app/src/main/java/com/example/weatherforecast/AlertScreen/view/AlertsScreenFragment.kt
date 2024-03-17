package com.example.weatherforecast.AlertScreen.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherforecast.Map.view.OpenStreetMapActivity
import com.example.weatherforecast.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AlertsScreenFragment : Fragment() {
    lateinit var fabAddAlerts : FloatingActionButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_alerts_screen, container, false)

        fabAddAlerts = view.findViewById(R.id.fab_add_alarm)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAddAlerts.setOnClickListener {
            val intent = Intent(requireActivity(), OpenStreetMapActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

    }

}