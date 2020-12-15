package com.lawmobile.presentation.utils

import com.safefleet.mobile.external_hardware.cameras.CameraService
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CameraHelperTest {

    private val wifiHelper: WifiHelper = mockk()
    private val cameraService: CameraService = mockk()
    private val cameraHelper = CameraHelper(cameraService, wifiHelper)
    
    @BeforeEach
    fun setUp(){
        clearAllMocks()
    }

    @Test
    fun testSetInstance() {
        CameraHelper.setInstance(cameraHelper)
        Assert.assertEquals(CameraHelper.getInstance(), cameraHelper)
    }

    @Test
    fun testCheckWithAlertIfTheCameraIsConnectedSuccess() {
        every { wifiHelper.getGatewayAddress() } returns ""
        every { cameraService.isCameraConnected(any()) } returns true
        Assert.assertTrue(cameraHelper.checkWithAlertIfTheCameraIsConnected())
    }

    @Test
    fun testCheckWithAlertIfTheCameraIsConnectedFailed() {
        every { wifiHelper.getGatewayAddress() } returns ""
        every { cameraService.isCameraConnected(any()) } returns false
        Assert.assertFalse(cameraHelper.checkWithAlertIfTheCameraIsConnected())
    }
}