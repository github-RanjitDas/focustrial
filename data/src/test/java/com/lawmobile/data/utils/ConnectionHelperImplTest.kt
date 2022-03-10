package com.lawmobile.data.utils

import com.lawmobile.body_cameras.CameraService
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

class ConnectionHelperImplTest {

    private val cameraService: CameraService = mockk(relaxed = true) {
        every { isCameraConnected("10.10.10.10") } returns true
        every { isCameraConnected("") } returns false
        coEvery { disconnectCamera() } returns Result.Success(Unit)
    }
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }
    private val connectionHelperImpl = ConnectionHelperImpl(cameraServiceFactory)

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

    @Test
    fun testDisconnectCameraFlow() {
        runBlocking { connectionHelperImpl.disconnectCamera() }
        coVerify { cameraService.disconnectCamera() }
    }
}
