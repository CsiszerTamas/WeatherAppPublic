package com.cst.data.di

import com.cst.data.mapper.WeatherMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MapperModule {

    @Singleton
    @Provides
    fun providesApplicationMapper(): WeatherMapper {
        return WeatherMapper()
    }
}

