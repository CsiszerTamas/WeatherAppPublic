package com.cst.data.di

import com.cst.data.di.qualifiers.ApiHttpClient
import com.cst.data.di.qualifiers.ApiRetrofit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private const val READ_TIME_OUT = 30L
    private const val CONNECT_TIME_OUT = 30L

    @Provides
    fun providesHttpClient(): OkHttpClient {
        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        okHttpBuilder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
        okHttpBuilder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)

        return okHttpBuilder.build()
    }

    @ApiHttpClient
    @Singleton
    @Provides
    fun providesApiHttpClient(okHttpClient: OkHttpClient): OkHttpClient {
        val okHttpBuilder = okHttpClient.newBuilder()
        okHttpBuilder.addInterceptor(Interceptor { chain ->

            val original = chain.request().newBuilder().apply {
                method(chain.request().method, chain.request().body)
            }

            chain.proceed(original.build())
        })

        return okHttpBuilder.build()
    }

    @Singleton
    @Provides
    fun providesGsonBuilder(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    fun providesDefaultRetrofit(
        gson: Gson,
        okHttpClient: OkHttpClient,
        baseUrl: String
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @ApiRetrofit
    @Singleton
    @Provides
    fun providesApiRetrofit(gson: Gson, @ApiHttpClient okHttpClient: OkHttpClient): Retrofit {
        return providesDefaultRetrofit(gson, okHttpClient, BASE_URL)
    }
}
