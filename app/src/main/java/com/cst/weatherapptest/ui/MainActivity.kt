package com.cst.weatherapptest.ui

import android.app.UiModeManager.MODE_NIGHT_NO
import android.app.UiModeManager.MODE_NIGHT_YES
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.cst.weatherapptest.R
import com.cst.weatherapptest.util.storage.PreferenceHelper
import com.cst.weatherapptest.util.storage.PreferenceHelper.get
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceHelper: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureAppTheme()
    }

    private fun configureAppTheme() {
        when (getSelectedTheme()) {
            MODE_NIGHT_NO -> {
                // Display app in LIGHT mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.Theme_WeatherAppTest_TransparentLightTheme)
            }
            MODE_NIGHT_YES -> {
                // Display app in NIGHT/DARK mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                setTheme(R.style.Theme_WeatherAppTest_TransparentDarkTheme)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                setTheme(R.style.Theme_WeatherAppTest_TransparentLightTheme)
            }
        }
    }

    private fun getSelectedTheme(): Int? {
        return preferenceHelper[PreferenceHelper.SELECTED_THEME, MODE_NIGHT_NO]
    }
}
