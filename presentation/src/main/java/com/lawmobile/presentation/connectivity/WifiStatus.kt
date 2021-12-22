package com.lawmobile.presentation.connectivity

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import com.lawmobile.domain.entities.CameraInfo
import javax.inject.Inject

class WifiStatus @Inject constructor(private val connectivityManager: ConnectivityManager) :
    LiveData<Boolean>() {

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            if (CameraInfo.isOfficerLogged) postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()

        val builder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
    }
}
