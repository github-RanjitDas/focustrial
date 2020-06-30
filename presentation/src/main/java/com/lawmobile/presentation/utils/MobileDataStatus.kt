package com.lawmobile.presentation.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

class MobileDataStatus(application: Application) : LiveData<Boolean>() {

    private val connectivityManager: ConnectivityManager by lazy {
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            postValue(true)
        }

        override fun onLost(network: Network?) {
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        value = false

        val builder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}