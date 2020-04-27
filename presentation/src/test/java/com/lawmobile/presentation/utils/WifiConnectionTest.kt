@file:Suppress("DEPRECATION")

package com.lawmobile.presentation.utils

import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.wifi.*
import android.os.Build
import android.util.Base64
import com.lawmobile.presentation.utils.WifiConnection.Companion.SECRET_CAMERA
import io.mockk.*
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.lang.reflect.Field
import java.lang.reflect.Modifier


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WifiConnectionTest {

    companion object {
        const val IP_NUMBER = 19572928
        const val DEFAULT_SSID = "X57014694"
        const val SDK_P = 28
        const val SDK_Q = 29
    }

    private val dhcpInfoMock: DhcpInfo = mockk()

    private val connectionInfoMock: WifiInfo = mockk {
        every { ipAddress } returns IP_NUMBER
        every { ssid } returns DEFAULT_SSID
    }

    private val scanResult: ScanResult = mockk {
        SSID = DEFAULT_SSID
    }

    private val wifiManager: WifiManager = mockk {
        every { dhcpInfo } returns dhcpInfoMock
        every { connectionInfo } returns connectionInfoMock
        every { setWifiEnabled(any()) } returns true
        every { reconnect() } returns true
        every { disconnect() } returns true
        every { addNetwork(any()) } returns 1
        every { enableNetwork(any(), any()) } returns true
        every { scanResults } returns listOf(scanResult)
    }

    private val wifiConfiguration: WifiConfiguration = mockk {
        allowedKeyManagement = mockk {
            every { set(any()) } just Runs
        }
        networkId = 1
    }

    private val connectivityManager: ConnectivityManager = mockk()

    private val wifiConnection = WifiConnection(wifiManager, wifiConfiguration, connectivityManager)

    @BeforeEach
    fun setUp() {
        mockkStatic(Base64::class)
        every { Base64.decode(SECRET_CAMERA, Base64.DEFAULT) } returns "12345".toByteArray()
        every { Base64.decode("12345".toByteArray(), Base64.DEFAULT) } returns "12345".toByteArray()
    }

    @Test
    fun testConnectionWithHotspotCamera() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_P)
        every { wifiManager.isWifiEnabled } returns true
        every { wifiManager.configuredNetworks } returns emptyList()
        wifiConnection.connectionWithHotspotCamera(DEFAULT_SSID) {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun testConnectionWithHotspotCameraAndroidQFailed() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_Q)
        wifiConnection.connectionWithHotspotCamera("") {
            Assert.assertFalse(it)
        }
    }



    @Throws(Exception::class)
    fun setFinalStatic(field: Field, newValue: Any?) {
        field.isAccessible = true
        val modifiersField: Field = Field::class.java.getDeclaredField("modifiers")
        modifiersField.isAccessible = true
        modifiersField.setInt(field, field.modifiers and Modifier.FINAL.inv())
        field.set(null, newValue)
    }
}