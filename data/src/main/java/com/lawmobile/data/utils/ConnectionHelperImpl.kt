package com.lawmobile.data.utils

import com.lawmobile.domain.utils.ConnectionHelper
import com.safefleet.mobile.external_hardware.cameras.CameraService

class ConnectionHelperImpl(private val cameraService: CameraService): ConnectionHelper {
    override fun isCameraConnected(ipAddress: String) = cameraService.isCameraConnected(ipAddress)
}