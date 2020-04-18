package com.lawmobile.presentation.utils

import android.annotation.SuppressLint
import android.net.wifi.WifiManager
import java.math.BigInteger
import java.net.Inet4Address


class WifiHelper(private val wifiManager: WifiManager) {

    @SuppressLint("MissingPermission")
    private fun getGatewayAddressByte() =
        BigInteger.valueOf(wifiManager.dhcpInfo.gateway.toLong()).toByteArray()

    fun getGatewayAddress(): String {
        return try {
            val reverseArrayIpAddress = getGatewayAddressByte().reversedArray()
            Inet4Address.getByAddress(reverseArrayIpAddress).hostAddress
        } catch (e: Exception) {
            ""
        }
    }

    fun getIpAddress(): String {
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
    private fun getSSIDWiFi(): String = wifiManager.connectionInfo.ssid.replace("\"", "")

    fun isEqualsValueWithSSID(value: String): Boolean = getSSIDWiFi() == value

}