package com.lawmobile.data.utils

import com.lawmobile.data.mappers.NotificationResponseMapper
import com.lawmobile.domain.entities.DomainNotification
import com.lawmobile.domain.utils.ConnectionHelper
import com.safefleet.mobile.external_hardware.cameras.CameraService

class ConnectionHelperImpl(cameraServiceFactory: CameraServiceFactory) : ConnectionHelper {
    private val cameraService: CameraService = cameraServiceFactory.create()
    override fun isCameraConnected(ipAddress: String) = cameraService.isCameraConnected(ipAddress)
    override fun reviewNotificationInCamera(notificationCallback: (DomainNotification) -> Unit) {
        cameraService.arriveNotificationFromCamera = {
            notificationCallback.invoke(NotificationResponseMapper.cameraToDomain(it))
        }
    }
}
