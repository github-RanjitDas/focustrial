package com.lawmobile.presentation.utils

import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WifiHelperTest {

    companion object {
        const val IP_NUMBER = 19572928
        const val DEFAULT_GATEWAY_ADDRESS = "192.168.42.1"
        const val DEFAULT_SSID = "X57014694"
    }

    private val dhcpInfoMock: DhcpInfo = mockk()

    private val connectionInfoMock: WifiInfo = mockk {
        every { ipAddress } returns IP_NUMBER
        every { ssid } returns DEFAULT_SSID
    }

    private val wifiManager: WifiManager = mockk {
        every { dhcpInfo } returns dhcpInfoMock
        every { connectionInfo } returns connectionInfoMock
    }

    private val connectivityManager: ConnectivityManager = mockk(relaxed = true)

    private val wifiHelper = WifiHelper(wifiManager, connectivityManager)

    @BeforeEach
    fun setUp() {
        dhcpInfoMock.gateway =
            IP_NUMBER
    }

    @Test
    fun testGetGatewayAddress() {
        every { wifiManager.dhcpInfo } returns dhcpInfoMock
        Assert.assertEquals(DEFAULT_GATEWAY_ADDRESS, wifiHelper.getGatewayAddress())
    }

    @Test
    fun testGetGatewayAddressError() {
        every { wifiManager.dhcpInfo } throws Exception("")
        Assert.assertEquals("", wifiHelper.getGatewayAddress())
    }

    @Test
    fun testGetIpAddress() {
        every { wifiManager.dhcpInfo } returns dhcpInfoMock
        Assert.assertEquals(DEFAULT_GATEWAY_ADDRESS, wifiHelper.getIpAddress())
    }

    @Test
    fun testGetIpAddressError() {
        every { wifiManager.connectionInfo } throws Exception()
        Assert.assertEquals("", wifiHelper.getIpAddress())
    }

    @Test
    fun testIsEqualsValueWithSSID() {
        every { wifiManager.dhcpInfo } returns dhcpInfoMock
        Assert.assertTrue(wifiHelper.isEqualsValueWithSSID(DEFAULT_SSID))
    }

    @Test
    fun testIsWifiEnabledTrue() {
        every { wifiManager.isWifiEnabled } returns true
        Assert.assertTrue(wifiHelper.isWifiEnable())
    }

    @Test
    fun testIsWifiEnabled() {
        every { wifiManager.isWifiEnabled } returns false
        Assert.assertFalse(wifiHelper.isWifiEnable())
    }
}
