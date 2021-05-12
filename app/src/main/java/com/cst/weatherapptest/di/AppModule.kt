package com.cst.weatherapptest.di

import android.content.Context
import android.content.SharedPreferences
import android.location.Geocoder
import com.cst.data.local.CurrentLocationDao
import com.cst.data.local.ForecastDao
import com.cst.data.local.LocationWithWeatherDao
import com.cst.weatherapptest.local.WeatherDatabase
import com.cst.weatherapptest.util.connection.ConnectionVerificationService
import com.cst.weatherapptest.util.storage.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesPreferenceHelper(
        @ApplicationContext context: Context,
    ): SharedPreferences {
        return PreferenceHelper.defaultPrefs(context)
    }

    @Singleton
    @Provides
    fun providesGeoCoder(@ApplicationContext context: Context): Geocoder {
        return Geocoder(context, Locale.ENGLISH)
    }

    @Singleton
    @Provides
    fun provideLocationWithWeatherDao(@ApplicationContext appContext: Context): LocationWithWeatherDao {
        return WeatherDatabase.getInstance(appContext).locationWithWeatherDao
    }

    @Singleton
    @Provides
    fun provideForecastDao(@ApplicationContext appContext: Context): ForecastDao {
        return WeatherDatabase.getInstance(appContext).forecastDao
    }

    @Singleton
    @Provides
    fun provideCurrentLocationDao(@ApplicationContext appContext: Context): CurrentLocationDao {
        return WeatherDatabase.getInstance(appContext).currentLocationDao
    }

    @Singleton
    @Provides
    fun providesConnectionVerificationService(): ConnectionVerificationService {
        return ConnectionVerificationService.getInstance()
    }
}
