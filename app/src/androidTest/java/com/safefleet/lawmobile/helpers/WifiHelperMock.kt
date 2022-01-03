package com.safefleet.lawmobile.helpers

import com.lawmobile.presentation.connectivity.WifiHelper
import com.safefleet.lawmobile.testData.TestLoginData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WifiHelperMock : WifiHelper {

    override val isWifiSignalLow: Flow<Boolean>
        get() = flow { emit(false) }

    override fun isWifiEnable(): Boolean {
        return if (MockUtils.wifiEnabled) {
            true
        } else {
            MockUtils.wifiEnabled = true
            false
        }
    }

    override fun getGatewayAddress(): String {
        return "192.168.42.1"
    }

    override fun getIpAddress(): String {
        return "192.168.42.2"
    }

    override fun suggestWiFiNetwork(
        networkName: String,
        networkPassword: String,
        connectionCallback: (connected: Boolean) -> Unit
    ) {
        connectionCallback.invoke(MockUtils.suggestWifiConnected)
    }

    override fun getSSIDWiFi(): String {
        return MockUtils.cameraSSID
    }

    override fun isEqualsValueWithSSID(value: String): Boolean {

        return when (value) {
            TestLoginData.SSID_X1.value -> true
            TestLoginData.SSID_X2.value -> true
            TestLoginData.INVALID_SSID.value -> false

            else -> false
        }
    }
}
