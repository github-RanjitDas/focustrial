package com.lawmobile.presentation.utils

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import com.lawmobile.domain.entities.CameraInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.math.BigInteger
import java.net.Inet4Address

open class WifiHelper(private val wifiManager: WifiManager) {

    val isWifiSignalLow = flow {
        while (CameraInfo.isOfficerLogged) {
            delay(DELAY_ON_READING_SIGNAL)
            val isSignalLevelLow = getSignalLevel() == LOW_SIGNAL_LEVEL
            emit(isSignalLevelLow)
        }
    }

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

    private fun getSignalLevel(): Int {
        val connectionInfo = wifiManager.connectionInfo
        return wifiManager.calculateSignalLevel(connectionInfo.rssi)
    }

    @SuppressLint("MissingPermission")
    private fun getIpAddressByteArray() =
        BigInteger.valueOf(wifiManager.connectionInfo.ipAddress.toLong()).toByteArray()

    @SuppressLint("MissingPermission")
    fun getSSIDWiFi(): String = wifiManager.connectionInfo.ssid.replace("\"", "")

    open fun isEqualsValueWithSSID(value: String): Boolean = getSSIDWiFi() == value

    companion object {
        private const val LOW_SIGNAL_LEVEL = 0
        private const val DELAY_ON_READING_SIGNAL = 1000L
    }
}
