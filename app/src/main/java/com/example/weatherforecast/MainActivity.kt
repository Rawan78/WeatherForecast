package com.example.weatherforecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherforecast.HomeScreen.view.HomeScreenFragment
import androidx.fragment.app.Fragment
import com.example.weatherforecast.AlertScreen.view.AlertsScreenFragment
import com.example.weatherforecast.FavoriteScreen.view.FavoritesScreenFragment
import com.example.weatherforecast.Settings.view.SettingsScreenFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        val homeScreenFragment = HomeScreenFragment()
        val settingsScreenFragment = SettingsScreenFragment()
        val favoritesScreenFragment = FavoritesScreenFragment()
        val alertsScreenFragment = AlertsScreenFragment()

        setCurrentFragment(homeScreenFragment)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.home->setCurrentFragment(homeScreenFragment)
                R.id.alerts->setCurrentFragment(alertsScreenFragment)
                R.id.settings->setCurrentFragment(settingsScreenFragment)
                R.id.favorite->setCurrentFragment(favoritesScreenFragment)
            }
            true
        }

    }
    private fun setCurrentFragment(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
}