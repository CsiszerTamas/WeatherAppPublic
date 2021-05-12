package com.cst.weatherapptest

import android.app.Application
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WeatherApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Use for local database inspection
        Stetho.initializeWithDefaults(this)
    }
}
