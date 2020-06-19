@file:Suppress("DEPRECATION")

package com.lawmobile.presentation.utils

import android.annotation.SuppressLint
import android.net.*
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi

@SuppressLint("MissingPermission")
class WifiConnection(
    private val wifiManager: WifiManager,
    private val wifiConfiguration: WifiConfiguration,
    private val connectivityManager: ConnectivityManager,
    private val wifiNetworkSpecifier: WifiNetworkSpecifier.Builder?,
    private val networkRequest: NetworkRequest.Builder
) {

    fun isWifiEnable(): Boolean {

        var isWifiEnable = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectivityManager.allNetworks.forEach { network ->
                connectivityManager.getNetworkInfo(network).apply {
                    if (this != null && type == ConnectivityManager.TYPE_WIFI) {
                        isWifiEnable = isWifiEnable or isConnected
                    }
                }
            }
        } else {
            isWifiEnable = wifiManager.isWifiEnabled
        }

        return isWifiEnable
    }

    fun connectionWithHotspotCamera(
        ssid: String,
        isConnectedSuccess: (connected: Boolean) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectWifiWithNetworkSpecifier(ssid, isConnectedSuccess)
        } else {
            connectWifiWithWifiConfiguration(ssid, isConnectedSuccess)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectWifiWithNetworkSpecifier(
        ssid: String,
        isConnectedSuccess: (connected: Boolean) -> Unit
    ) {
        wifiManager.startScan()
        waitSecondsWhileTheWifiIsConnected()
        var network: ScanResult? = null
        wifiManager.scanResults.forEach {
            if (it.SSID == ssid) network = it
        }

        if (network == null || wifiNetworkSpecifier == null) {
            isConnectedSuccess.invoke(false)
            return
        }

        val specifier = wifiNetworkSpecifier
            .setSsid(network!!.SSID)
            .setWpa2Passphrase(getWifiSecret())
            .setBssid(MacAddress.fromString(network!!.BSSID))
            .build()

        val request = networkRequest
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(specifier)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network?) {
                connectivityManager.bindProcessToNetwork(network)
                isConnectedSuccess.invoke(true)
            }

            override fun onUnavailable() {
                isConnectedSuccess.invoke(false)
            }
        }

        connectivityManager.requestNetwork(request, networkCallback)
    }

    private fun connectWifiWithWifiConfiguration(
        ssid: String,
        isConnectedSuccess: (connected: Boolean) -> Unit
    ) {

        enableWifi()
        var networkId = getIfExistNetworkId(ssid)
        if (networkId == null) {
            setWifiConfiguration(ssid)
            wifiManager.addNetwork(wifiConfiguration)
            networkId = wifiConfiguration.networkId
        } else {
            setWifiConfiguration(ssid)
            wifiManager.updateNetwork(wifiConfiguration)
        }

        wifiManager.disconnect()
        wifiManager.enableNetwork(networkId, true)
        waitSecondsWhileTheWifiIsConnected()
        if (!existNetwork(ssid)) {
            isConnectedSuccess.invoke(false)
            return
        }

        val isReconnectWifi = wifiManager.reconnect()
        isConnectedSuccess.invoke(isReconnectWifi)
    }

    private fun waitSecondsWhileTheWifiIsConnected() {
        Thread.sleep(2000)
    }

    private fun enableWifi() {
        if (!wifiManager.isWifiEnabled)
            wifiManager.isWifiEnabled = true
    }

    private fun existNetwork(ssid: String): Boolean {
        var scanResult: ScanResult? = null
        wifiManager.scanResults.forEach {
            if (it.SSID == ssid) scanResult = it
        }

        return scanResult != null || wifiManager.scanResults.size == 0
    }

    private fun getIfExistNetworkId(ssid: String): Int? {
        val configuredNetworksSaved = wifiManager.configuredNetworks
        configuredNetworksSaved.forEach {

            if (it.SSID == "\"$ssid\"") return it.networkId
        }

        return null
    }

    private fun setWifiConfiguration(ssid: String) {
        wifiConfiguration.SSID = "\"$ssid\""
        wifiConfiguration.preSharedKey = "\"${getWifiSecret()}\""
        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        wifiConfiguration.priority = CONNECTION_PRIORITY
    }

    private fun getWifiSecret(): String {
        var buffer: ByteArray = Base64.decode(SECRET_CAMERA, Base64.DEFAULT)
        buffer = Base64.decode(String(buffer).toByteArray(), Base64.DEFAULT)
        buffer = Base64.decode(String(buffer).toByteArray(), Base64.DEFAULT)
        return String(buffer)
    }

    companion object {
        const val SECRET_CAMERA = "VFZSSmVrNUVWVEpPZW1jMVRVRTlQUT09"
        const val CONNECTION_PRIORITY = 999999999
    }
}