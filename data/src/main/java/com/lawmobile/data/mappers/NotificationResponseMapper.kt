package com.lawmobile.data.mappers

import com.lawmobile.data.utils.DateHelper.dateToString
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.enums.NotificationType
import com.safefleet.mobile.external_hardware.cameras.entities.NotificationResponse
import java.util.Date

object NotificationResponseMapper {
    fun cameraToDomain(notificationResponse: NotificationResponse): CameraEvent {
        val eventValue = notificationResponse.type.split(":").last()
        val eventType = NotificationType.getByValue(eventValue).getTypeOfEvent()
        val eventName = if (eventType == EventType.NOTIFICATION) "Notification" else "cameraEvent"
        val eventTag = when (notificationResponse.type.split(":").first()) {
            EventTag.WARNING.value -> EventTag.WARNING
            EventTag.ERROR.value -> EventTag.ERROR
            else -> EventTag.INFORMATION
        }

        return CameraEvent(
            name = eventName,
            eventType = eventType,
            eventTag = eventTag,
            value = eventValue,
            date = dateToString(Date(), "MM/dd/yyyy HH:mm:ss")
        )
    }
}
