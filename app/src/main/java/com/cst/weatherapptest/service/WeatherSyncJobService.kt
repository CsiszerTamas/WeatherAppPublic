package com.cst.weatherapptest.service

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.util.Log
import com.cst.domain.usecase.sync.SyncWeatherDataUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val JOB_ID = 97

/**
 *  A background service which is responsible for keeping the favorite location data and
 *  the forecast data for the favorite locations up to date
 */
@AndroidEntryPoint
class WeatherSyncJobService : JobService() {

    @Inject
    lateinit var syncWeatherDataUseCase: SyncWeatherDataUseCase

    companion object {

        fun schedule(context: Context, intervalMillis: Long) {
            Log.d("DEBUG_", "WeatherSyncJobService schedule $JOB_ID")
            val jobScheduler =
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val componentName = ComponentName(context, WeatherSyncJobService::class.java)
            val builder = JobInfo.Builder(JOB_ID, componentName)
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            builder.setPeriodic(intervalMillis)
            jobScheduler.schedule(builder.build())
        }

        fun cancel(context: Context) {
            Log.d("DEBUG_", "WeatherSyncJobService cancel $JOB_ID")
            val jobScheduler =
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            jobScheduler.cancel(JOB_ID)
        }

    }

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("DEBUG_", "WeatherSyncJobService onStopJob $JOB_ID")
        return false
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Log.d("DEBUG_", "WeatherSyncJobService onStartJob $JOB_ID")

        syncWeatherDataUseCase.execute(
            onSuccess = {
                Log.d("DEBUG_", "WeatherSyncJobService syncWeatherDataUseCase executed, onSuccess")
                this.jobFinished(params, false)
            },
            onError = {
                Log.d(
                    "DEBUG_",
                    "WeatherSyncJobService syncWeatherDataUseCase executed, onError: $it"
                )
                this.jobFinished(params, false)
                it.printStackTrace()
            }
        )
        return false
    }
}
