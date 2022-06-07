package com.lawmobile.data.utils

import com.lawmobile.body_cameras.CameraService
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.CameraType

class CameraServiceFactoryImpl(
    val x1CameraServiceImpl: CameraService,
    val x2CameraServiceImpl: CameraService
) : CameraServiceFactory {
    override fun create(): CameraService {
        return when (CameraInfo.cameraType) {
            CameraType.X1 -> x1CameraServiceImpl
            CameraType.X2 -> x2CameraServiceImpl
        }
    }
}
