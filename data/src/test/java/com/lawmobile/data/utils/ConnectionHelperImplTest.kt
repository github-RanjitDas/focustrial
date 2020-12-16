package com.lawmobile.data.utils

import com.safefleet.mobile.external_hardware.cameras.CameraService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.jupiter.api.Test

class ConnectionHelperImplTest {

    private val cameraService: CameraService = mockk(relaxed = true) {
        every { isCameraConnected("10.10.10.10") } returns true
        every { isCameraConnected("") } returns false
    }
    private val connectionHelperImpl = ConnectionHelperImpl(cameraService)

    @Test
    fun testFlowIsCameraConnected() {
        connectionHelperImpl.isCameraConnected("10.10.10.10")
        verify { cameraService.isCameraConnected("10.10.10.10") }
    }

    @Test
    fun testSuccessIsCameraConnected() {
        val isCameraConnection = connectionHelperImpl.isCameraConnected("10.10.10.10")
        Assert.assertEquals(isCameraConnection, true)
    }

    @Test
    fun testFalseIsCameraConnected() {
        val isCameraConnection = connectionHelperImpl.isCameraConnected("")
        Assert.assertEquals(isCameraConnection, false)
    }
}