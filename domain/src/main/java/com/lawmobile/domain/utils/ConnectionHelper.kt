package com.lawmobile.domain.utils

import com.lawmobile.domain.entities.DomainNotification

interface ConnectionHelper {
    fun isCameraConnected(ipAddress: String): Boolean
    fun reviewNotificationInCamera(notificationCallback: (DomainNotification) -> Unit)
}
