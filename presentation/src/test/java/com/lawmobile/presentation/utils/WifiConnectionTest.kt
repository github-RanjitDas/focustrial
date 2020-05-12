@file:Suppress("DEPRECATION")

package com.lawmobile.presentation.utils

import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.NetworkRequest
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
        every { startScan() } returns true
    }

    private val wifiConfiguration: WifiConfiguration = mockk {
        allowedKeyManagement = mockk {
            every { set(any()) } just Runs
        }
        networkId = 1
    }

    private val connectivityManager: ConnectivityManager = mockk(relaxed = true)

    private val wifiNetworkSpecifier = mockk<WifiNetworkSpecifier.Builder> {
        every { setSsid(DEFAULT_SSID) } returns mockk(relaxed = true)
        every { setWpa2Passphrase("12345") } returns mockk(relaxed = true)
        every { setBssid(any()) } returns mockk(relaxed = true)
        every { build() } returns mockk(relaxed = true)
    }

    private val networkRequest = mockk<NetworkRequest.Builder> {
        every { addTransportType(any()) } returns mockk(relaxed = true)
        every { setNetworkSpecifier(wifiNetworkSpecifier.build()) } returns mockk(relaxed = true)
        every { build() } returns mockk(relaxed = true)
    }

    private val wifiConnection = WifiConnection(
        wifiManager,
        wifiConfiguration,
        connectivityManager,
        wifiNetworkSpecifier,
        networkRequest
    )

    @BeforeEach
    fun setUp() {
        mockkStatic(Base64::class)
        every { Base64.decode(SECRET_CAMERA, Base64.DEFAULT) } returns "12345".toByteArray()
        every { Base64.decode("12345".toByteArray(), Base64.DEFAULT) } returns "12345".toByteArray()
    }

    @Test
    fun testConnectionWithHotspotCameraAndroidPSuccess() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_P)
        every { wifiManager.isWifiEnabled } returns true
        every { wifiManager.configuredNetworks } returns emptyList()
        wifiConnection.connectionWithHotspotCamera(DEFAULT_SSID) {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun testConnectionWithHotspotCameraAndroidPVerifyWiFiSecretCorrectly() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_P)
        every { wifiManager.isWifiEnabled } returns false
        every { wifiManager.configuredNetworks } returns emptyList()
        wifiConnection.connectionWithHotspotCamera(DEFAULT_SSID) {}
        Assert.assertEquals(wifiConfiguration.preSharedKey, "\"12345\"")
    }

    @Test
    fun testConnectionWithHotspotCameraAndroidPWifiEnable() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_P)
        every { wifiManager.isWifiEnabled } returns false
        every { wifiManager.configuredNetworks } returns emptyList()
        wifiConnection.connectionWithHotspotCamera(DEFAULT_SSID) {}
        verify { wifiManager.isWifiEnabled }
    }

    @Test
    fun testConnectionWithHotspotCameraAndroidPSuccessWithSavedWifiConfiguration() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_P)
        every { wifiManager.isWifiEnabled } returns true
        val wifiConfiguration: WifiConfiguration = mockk()
        wifiConfiguration.SSID = DEFAULT_SSID
        wifiConfiguration.networkId = 1
        every { wifiManager.configuredNetworks } returns listOf(wifiConfiguration)
        wifiConnection.connectionWithHotspotCamera(DEFAULT_SSID) {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun testConnectionWithHotspotCameraAndroidPVerifyNetworkId() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_P)
        every { wifiManager.isWifiEnabled } returns true
        val wifiConfiguration: WifiConfiguration = mockk()
        wifiConfiguration.SSID = DEFAULT_SSID
        wifiConfiguration.networkId = 1
        every { wifiManager.configuredNetworks } returns listOf(wifiConfiguration)
        wifiConnection.connectionWithHotspotCamera(DEFAULT_SSID) {}
        verify { wifiManager.enableNetwork(1, true) }
    }


    @Test
    fun testConnectionWithHotspotCameraAndroidPSuccessWithAnotherWifiConfiguration() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_P)
        every { wifiManager.isWifiEnabled } returns true
        val wifiConfiguration: WifiConfiguration = mockk()
        wifiConfiguration.SSID = "OtherWifi"
        every { wifiManager.configuredNetworks } returns listOf(wifiConfiguration)
        wifiConnection.connectionWithHotspotCamera(DEFAULT_SSID) {
            Assert.assertTrue(it)
        }
    }

    @Test
    fun testConnectionWithHotspotCameraAndroidPFailed() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_P)
        every { wifiManager.isWifiEnabled } returns true
        every { wifiManager.configuredNetworks } returns emptyList()
        wifiConnection.connectionWithHotspotCamera("AnotherSSID") {
            Assert.assertFalse(it)
        }
    }

    @Test
    fun testConnectionWithHotspotCameraAndroidQSuccess() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_Q)
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


    @Test
    fun testConnectionWithHotspotCameraAndroidQFailedByNetworkSpecifier() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_Q)
        val wifiConnection = WifiConnection(
            wifiManager,
            wifiConfiguration,
            connectivityManager,
            null,
            networkRequest
        )
        wifiConnection.connectionWithHotspotCamera(DEFAULT_SSID) {
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
    @Test
    fun testConnectionWithHotspotCameraAndroidQCallStartScan() {
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_Q)
        val wifiConnection = WifiConnection(
            wifiManager,
            wifiConfiguration,
            connectivityManager,
            null,
            networkRequest
        )
        wifiConnection.connectionWithHotspotCamera(DEFAULT_SSID) {}
        verify { wifiManager.startScan() }
    }
    @Test
    fun testConnectionWithHotspotCameraAndroidQCallScanResultTrue() {
        every { wifiManager.startScan() } returns true
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_Q)
        val wifiConnection = WifiConnection(
            wifiManager,
            wifiConfiguration,
            connectivityManager,
            null,
            networkRequest
        )
        wifiConnection.connectionWithHotspotCamera(DEFAULT_SSID) {}
        Assert.assertTrue(wifiManager.startScan())
    }
    @Test
    fun testConnectionWithHotspotCameraAndroidQCallScanResultFalse() {
        every { wifiManager.startScan() } returns false
        mockkStatic(Build.VERSION::class)
        setFinalStatic(Build.VERSION::class.java.getField("SDK_INT"), SDK_Q)
        val wifiConnection = WifiConnection(
            wifiManager,
            wifiConfiguration,
            connectivityManager,
            null,
            networkRequest
        )
        wifiConnection.connectionWithHotspotCamera(DEFAULT_SSID) {}
        Assert.assertFalse(wifiManager.startScan())
    }
}
