package com.lawmobile.data.utils

import com.lawmobile.body_cameras.x1.X1CameraServiceImpl
import com.lawmobile.body_cameras.x2.X2CameraServiceImpl
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.CameraType
import io.mockk.mockk
import org.junit.Assert
import org.junit.jupiter.api.Test

class CameraServiceFactoryImplTest {

    private val x1cameraService: X1CameraServiceImpl = mockk()
    private val x2cameraService: X2CameraServiceImpl = mockk()

    private val cameraServiceFactoryImpl = CameraServiceFactoryImpl(x1cameraService, x2cameraService)

    @Test
    fun testCameraServiceIsX1() {
        CameraInfo.setCamera(CameraType.X1)
        val cameraX1 = cameraServiceFactoryImpl.create()
        Assert.assertEquals(cameraX1, x1cameraService)
    }

    @Test
    fun testCameraServiceIsX2() {
        CameraInfo.setCamera(CameraType.X2)
        val cameraX1 = cameraServiceFactoryImpl.create()
        Assert.assertEquals(cameraX1, x2cameraService)
    }
}
