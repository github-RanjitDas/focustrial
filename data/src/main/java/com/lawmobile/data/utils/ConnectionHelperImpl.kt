package com.lawmobile.data.utils

import com.lawmobile.body_cameras.CameraService
import com.lawmobile.data.mappers.impl.NotificationResponseMapper.toDomain
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.utils.ConnectionHelper
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ConnectionHelperImpl(private val cameraServiceFactory: CameraServiceFactory) :
    ConnectionHelper {
    private var cameraService: CameraService = cameraServiceFactory.create()

    override fun isCameraConnected(ipAddress: String) = cameraService.isCameraConnected(ipAddress)
    override fun isCameraConnectedV2(ipAddress: String) = cameraService.isCameraConnectedV2(ipAddress)
    override fun onCameraEvent(callback: (CameraEvent) -> Unit) {
        cameraService.arriveNotificationFromCamera = {
            callback.invoke(it.toDomain())
        }
    }

    override suspend fun disconnectCamera(): Result<Unit> {
        return cameraService.disconnectCamera()
    }

    override fun reviewIfArriveNotificationInCMDSocket() {
        cameraService = cameraServiceFactory.create()
        cameraService.reviewIfArriveNotificationInCMDSocket()
    }

    override suspend fun resetViewFinder(ipAddressClient: String): Boolean {
        return cameraService.resetViewFinder(ipAddressClient)
    }
}
