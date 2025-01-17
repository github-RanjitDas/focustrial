package com.lawmobile.presentation.connectivity

import android.os.Handler
import kotlinx.coroutines.flow.Flow

interface WifiHelper {

    val isWifiSignalLow: Flow<Boolean>

    fun isWifiEnable(): Boolean

    fun getGatewayAddress(): String

    fun getIpAddress(): String

    fun suggestWiFiNetwork(
        handler: Handler,
        networkName: String,
        networkPassword: String,
        connectionCallback: (connected: Boolean) -> Unit
    )

    fun getSSIDWiFi(): String

    fun isEqualsValueWithSSID(value: String): Boolean
}
