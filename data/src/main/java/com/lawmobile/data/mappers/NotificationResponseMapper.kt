package com.lawmobile.data.mappers

import com.lawmobile.data.utils.DateHelper.dateToString
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.safefleet.mobile.external_hardware.cameras.entities.NotificationResponse
import java.util.Date

object NotificationResponseMapper {
    fun cameraToDomain(notificationResponse: NotificationResponse): CameraEvent {
        val typeNotification = when (notificationResponse.type.split(":").first()) {
            EventTag.WARNING.value -> EventTag.WARNING
            EventTag.ERROR.value -> EventTag.ERROR
            else -> EventTag.INFORMATION
        }
        val name = when (typeNotification) {
            EventTag.INFORMATION -> notificationResponse.type
            EventTag.WARNING, EventTag.ERROR ->
                notificationResponse.type.split(":").last()
        }
        return CameraEvent(
            name,
            EventType.NOTIFICATION,
            typeNotification,
            notificationResponse.param,
            dateToString(Date(), "MM/dd/yyyy HH:mm:ss")
        )
    }
}
