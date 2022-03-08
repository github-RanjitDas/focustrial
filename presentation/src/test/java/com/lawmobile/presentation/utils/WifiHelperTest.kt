package com.lawmobile.presentation.utils

import android.net.ConnectivityManager
import android.net.DhcpInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.presentation.utils.Build.getSDKVersion
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
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

    private val wifiHelper = WifiHelperImpl(wifiManager, connectivityManager)

    private val dispatcher = TestCoroutineDispatcher()
    private val job by lazy { Job() }
    private val testScope by lazy { TestCoroutineScope() }

    @AfterEach
    fun clean() = job.cancel()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        dhcpInfoMock.gateway = IP_NUMBER
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
        every { wifiManager.connectionInfo.ssid } returns DEFAULT_SSID
        every { wifiManager.dhcpInfo } returns dhcpInfoMock
        Assert.assertTrue(wifiHelper.isEqualsValueWithSSID(DEFAULT_SSID))
    }

    @Test
    fun testIsNotEqualsValueWithSSID() {
        every { wifiManager.connectionInfo.ssid } returns ""
        every { wifiManager.dhcpInfo } returns dhcpInfoMock
        Assert.assertFalse(wifiHelper.isEqualsValueWithSSID(DEFAULT_SSID))
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

    @Test
    fun isWifiSignalLowTrue() = runBlockingTest {
        testScope.launch {
            mockkObject(CameraInfo)
            mockkObject(Build)

            every { getSDKVersion() } returns Build.VERSION_CODES.R
            every { CameraInfo.isOfficerLogged } returns true
            every { wifiManager.connectionInfo.rssi } returns 4
            every { wifiManager.calculateSignalLevel(any()) } returns 1

            dispatcher.advanceTimeBy(1002)

            Assert.assertTrue(wifiHelper.isWifiSignalLow.first())

            verify {
                getSDKVersion()
                wifiManager.calculateSignalLevel(any())
                wifiManager.connectionInfo.rssi
                CameraInfo.isOfficerLogged
            }
        }
        delay(1000)
    }

    @Test
    fun isWifiSignalLowFalse() = runBlockingTest {
        testScope.launch {
            mockkObject(CameraInfo)
            mockkObject(Build)

            every { getSDKVersion() } returns Build.VERSION_CODES.R
            every { CameraInfo.isOfficerLogged } returns true
            every { wifiManager.connectionInfo.rssi } returns 4
            every { wifiManager.calculateSignalLevel(any()) } returns 1 andThen 4

            dispatcher.advanceTimeBy(1002)
            Assert.assertTrue(wifiHelper.isWifiSignalLow.first())
            Assert.assertFalse(wifiHelper.isWifiSignalLow.first())

            verify {
                wifiManager.calculateSignalLevel(any())
                wifiManager.connectionInfo.rssi
                CameraInfo.isOfficerLogged
            }
        }
        delay(1000)
    }
}
