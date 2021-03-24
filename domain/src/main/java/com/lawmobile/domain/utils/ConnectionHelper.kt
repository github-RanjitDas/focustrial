package com.lawmobile.domain.utils

import com.lawmobile.domain.entities.CameraEvent

interface ConnectionHelper {
    fun isCameraConnected(ipAddress: String): Boolean
    fun reviewNotificationInCamera(notificationCallback: (CameraEvent) -> Unit)
}
