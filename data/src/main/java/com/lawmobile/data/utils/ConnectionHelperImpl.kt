package com.lawmobile.data.utils

import com.lawmobile.data.mappers.NotificationResponseMapper
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.utils.ConnectionHelper
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ConnectionHelperImpl(cameraServiceFactory: CameraServiceFactory) : ConnectionHelper {
    private val cameraService: CameraService = cameraServiceFactory.create()
    override fun isCameraConnected(ipAddress: String) = cameraService.isCameraConnected(ipAddress)
    override fun onCameraEvent(callback: (CameraEvent) -> Unit) {
        cameraService.arriveNotificationFromCamera = {
            callback.invoke(NotificationResponseMapper.cameraToDomain(it))
        }
    }

    override suspend fun disconnectCamera(): Result<Unit> {
        return cameraService.disconnectCamera()
    }
}
