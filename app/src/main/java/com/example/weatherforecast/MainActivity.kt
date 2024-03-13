package com.example.weatherforecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherforecast.HomeScreen.view.HomeScreenFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = HomeScreenFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_home, fragment, "HomeScreenFragment")
                .commit()
        }
    }
}