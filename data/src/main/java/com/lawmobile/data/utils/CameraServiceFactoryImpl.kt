package com.lawmobile.data.utils

import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.CameraType
import com.safefleet.mobile.external_hardware.cameras.CameraService

class CameraServiceFactoryImpl(
    var x1CameraServiceImpl: CameraService,
    var x2CameraServiceImpl: CameraService
) : CameraServiceFactory {
    override fun create(): CameraService {
        return when (CameraInfo.cameraType) {
            CameraType.X1 -> x1CameraServiceImpl
            CameraType.X2 -> x2CameraServiceImpl
        }
    }
}
