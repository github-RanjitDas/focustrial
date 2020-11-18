package com.lawmobile.presentation.utils

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import java.math.BigInteger
import java.net.Inet4Address


open class WifiHelper(private val wifiManager: WifiManager) {

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

    @SuppressLint("MissingPermission")
    private fun getIpAddressByteArray() =
        BigInteger.valueOf(wifiManager.connectionInfo.ipAddress.toLong()).toByteArray()

    @SuppressLint("MissingPermission")
    fun getSSIDWiFi(): String = wifiManager.connectionInfo.ssid.replace("\"", "")

    open fun isEqualsValueWithSSID(value: String): Boolean = getSSIDWiFi() == value

}