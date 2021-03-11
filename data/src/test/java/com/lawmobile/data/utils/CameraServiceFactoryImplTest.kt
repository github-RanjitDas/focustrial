package com.lawmobile.data.utils

import com.lawmobile.domain.entities.CameraInfo
import com.safefleet.mobile.external_hardware.cameras.x1.X1CameraServiceImpl
import com.safefleet.mobile.external_hardware.cameras.x2.X2CameraServiceImpl
import io.mockk.mockk
import org.junit.Assert
import org.junit.jupiter.api.Test

class CameraServiceFactoryImplTest {

    private val x1cameraService: X1CameraServiceImpl = mockk()
    private val x2cameraService: X2CameraServiceImpl = mockk()

    private val cameraServiceFactoryImpl = CameraServiceFactoryImpl(x1cameraService, x2cameraService)

    @Test
    fun testCameraServiceIsX1() {
        CameraInfo.setCameraType("X571231231")
        val cameraX1 = cameraServiceFactoryImpl.create()
        Assert.assertEquals(cameraX1, x1cameraService)
    }

    @Test
    fun testCameraServiceIsX2() {
        CameraInfo.setCameraType("01010101001")
        val cameraX1 = cameraServiceFactoryImpl.create()
        Assert.assertEquals(cameraX1, x2cameraService)
    }
}
