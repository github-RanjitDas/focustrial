package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainNotification
import com.safefleet.mobile.external_hardware.cameras.entities.NotificationResponse

object NotificationResponseMapper {
    fun cameraToDomain(notificationResponse: NotificationResponse): DomainNotification {
        return DomainNotification(notificationResponse.type, notificationResponse.param)
    }
}
