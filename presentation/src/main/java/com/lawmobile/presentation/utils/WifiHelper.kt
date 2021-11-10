package com.lawmobile.presentation.utils

interface WifiHelper {

    fun isWifiEnable(): Boolean

    fun getGatewayAddress(): String

    fun getIpAddress(): String

    fun suggestWiFiNetwork(
        networkName: String,
        networkPassword: String,
        connectionCallback: (connected: Boolean) -> Unit
    )

    fun getSSIDWiFi(): String

    fun isEqualsValueWithSSID(value: String): Boolean
}
