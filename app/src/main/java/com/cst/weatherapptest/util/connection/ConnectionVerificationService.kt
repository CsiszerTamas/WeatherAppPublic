package com.cst.weatherapptest.util.connection

import android.content.Context
import android.net.ConnectivityManager

/**
 * Helper class to check if the device is a working internet connection
 */
class ConnectionVerificationService {

    companion object {

        // For Singleton instantiation
        @Volatile
        private var INSTANCE: ConnectionVerificationService? = null

        fun getInstance(): ConnectionVerificationService {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = createConnectionHelper()
                    INSTANCE = instance
                }
                return instance
            }
        }

        private fun createConnectionHelper(): ConnectionVerificationService {
            return ConnectionVerificationService()
        }
    }

    fun isConnectedToNetwork(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connectivityManager.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }
}
