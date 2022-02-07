package com.lawmobile.presentation.utils

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import android.os.Build
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.utils.Build.getSDKVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.math.BigInteger
import java.net.Inet4Address

open class WifiHelper(private val wifiManager: WifiManager) {

    private var isWifiSignalStateLow = false

    val isWifiSignalLow = flow {
        while (CameraInfo.isOfficerLogged) {
            delay(DELAY_ON_READING_SIGNAL)
            val isSignalLevelLow = getSignalLevel() <= LOW_SIGNAL_LEVEL
            if (isSignalLevelLow != isWifiSignalStateLow) {
                isWifiSignalStateLow = isSignalLevelLow
                emit(isSignalLevelLow)
            }
        }
    }.flowOn(Dispatchers.IO)

    fun isWifiEnable(): Boolean = wifiManager.isWifiEnabled

    @SuppressLint("MissingPermission")
    private fun getGatewayAddressByte() =
        BigInteger.valueOf(wifiManager.dhcpInfo.gateway.toLong()).toByteArray()

    open fun getGatewayAddress(): String {
        return try {
            val reverseArrayIpAddress = getGatewayAddressByte().reversedArray()
            Inet4Address.getByAddress(reverseArrayIpAddress).hostAddress
        } catch (e: Exception) {
            ""
        }
    }

    open fun getIpAddress(): String {
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

    @SuppressLint("MissingPermission")
    private fun getIpAddressByteArray() =
        BigInteger.valueOf(wifiManager.connectionInfo.ipAddress.toLong()).toByteArray()

    @SuppressLint("MissingPermission")
    fun getSSIDWiFi(): String = wifiManager.connectionInfo.ssid.replace("\"", "")

    open fun isEqualsValueWithSSID(value: String): Boolean = getSSIDWiFi() == value

    companion object {
        private const val SIGNAL_LEVELS = 5
        private const val LOW_SIGNAL_LEVEL = 1
        private const val DELAY_ON_READING_SIGNAL = 1000L
    }
}
