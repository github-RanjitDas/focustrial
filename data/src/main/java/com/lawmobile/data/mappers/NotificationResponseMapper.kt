package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainNotification
import com.lawmobile.domain.entities.NotificationType
import com.safefleet.mobile.external_hardware.cameras.entities.NotificationResponse

object NotificationResponseMapper {
    fun cameraToDomain(notificationResponse: NotificationResponse): DomainNotification {
        val typeNotification = when (notificationResponse.type.split(":").first()) {
            NotificationType.WARNING.value -> NotificationType.WARNING
            NotificationType.ERROR.value -> NotificationType.ERROR
            else -> NotificationType.INFORMATION
        }
        return DomainNotification(typeNotification, notificationResponse.param)
    }
}
