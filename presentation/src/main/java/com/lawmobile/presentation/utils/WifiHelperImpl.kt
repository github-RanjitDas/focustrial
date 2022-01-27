package com.lawmobile.presentation.utils

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import androidx.annotation.RequiresApi
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.utils.Build.getSDKVersion
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.lang.Thread.sleep
import java.math.BigInteger
import java.net.Inet4Address

class WifiHelperImpl(
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager
) : WifiHelper {

    private var isWifiSignalStateLow = false

    override val isWifiSignalLow = flow {
        while (CameraInfo.isOfficerLogged) {
            delay(DELAY_ON_READING_SIGNAL)
            val isSignalLevelLow = getSignalLevel() == LOW_SIGNAL_LEVEL
            if (isSignalLevelLow != isWifiSignalStateLow) {
                isWifiSignalStateLow = isSignalLevelLow
                emit(isSignalLevelLow)
            }
        }
    }

    override fun isWifiEnable(): Boolean = wifiManager.isWifiEnabled

    @SuppressLint("MissingPermission")
    private fun getGatewayAddressByte() =
        BigInteger.valueOf(wifiManager.dhcpInfo.gateway.toLong()).toByteArray()

    override fun getGatewayAddress(): String {
        return try {
            val reverseArrayIpAddress = getGatewayAddressByte().reversedArray()
            Inet4Address.getByAddress(reverseArrayIpAddress).hostAddress
        } catch (e: Exception) {
            ""
        }
    }

    override fun getIpAddress(): String {
        return try {
            val reverseArrayIpAddress = getIpAddressByteArray().reversedArray()
            Inet4Address.getByAddress(reverseArrayIpAddress).hostAddress
        } catch (e: Exception) {
            ""
        }
    }

    @SuppressLint("NewApi")
    @Suppress("DEPRECATION")
    private fun getSignalLevel(): Int {
        val connectionInfo = wifiManager.connectionInfo
        return if (getSDKVersion() >= Build.VERSION_CODES.R) {
            wifiManager.calculateSignalLevel(connectionInfo.rssi)
        } else {
            WifiManager.calculateSignalLevel(connectionInfo.rssi, SIGNAL_LEVELS)
        }
    }

    @Suppress("DEPRECATION")
    override fun suggestWiFiNetwork(
        networkName: String,
        networkPassword: String,
        connectionCallback: (connected: Boolean) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val specifier = getWifiNetworkSpecifier(networkName, networkPassword)
            val request = getNetworkRequest(specifier)
            val networkCallback = networkCallback(connectionCallback)
            connectivityManager.requestNetwork(request, networkCallback)
        } else {
            val wifiConfig = WifiConfiguration()
            wifiConfig.SSID = java.lang.String.format("\"%s\"", networkName)
            wifiConfig.preSharedKey = java.lang.String.format("\"%s\"", networkPassword)
            wifiConfig.priority = 1000000000
            val netId = wifiManager.addNetwork(wifiConfig)
            wifiManager.disconnect()
            wifiManager.enableNetwork(netId, true)
            wifiManager.reconnect()
            sleep(1000)
            connectionCallback(true)
        }
    }

    private fun networkCallback(isConnectedSuccess: (connected: Boolean) -> Unit) =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                connectivityManager.bindProcessToNetwork(network)
                isConnectedSuccess(true)
            }

            override fun onUnavailable() {
                isConnectedSuccess(false)
            }
        }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getNetworkRequest(specifier: WifiNetworkSpecifier): NetworkRequest =
        NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(specifier)
            .build()

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getWifiNetworkSpecifier(
        networkName: String,
        networkPassword: String
    ): WifiNetworkSpecifier =
        WifiNetworkSpecifier.Builder()
            .setSsid(networkName)
            .setWpa2Passphrase(networkPassword)
            .build()

    @SuppressLint("MissingPermission")
    private fun getIpAddressByteArray() =
        BigInteger.valueOf(wifiManager.connectionInfo.ipAddress.toLong()).toByteArray()

    @SuppressLint("MissingPermission")
    override fun getSSIDWiFi(): String = wifiManager.connectionInfo.ssid.replace("\"", "")

    override fun isEqualsValueWithSSID(value: String): Boolean = getSSIDWiFi() == value

    companion object {
        private const val SIGNAL_LEVELS = 5
        private const val LOW_SIGNAL_LEVEL = 0
        private const val DELAY_ON_READING_SIGNAL = 1000L
    }
}
