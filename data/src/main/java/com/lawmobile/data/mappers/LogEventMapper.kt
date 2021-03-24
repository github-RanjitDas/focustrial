package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent

object LogEventMapper {
    fun cameraToDomainNotificationList(logEventList: List<LogEvent>) =
        logEventList.map { cameraToDomainNotification(it) }

    private fun cameraToDomainNotification(logEvent: LogEvent) =
        logEvent.run {
            var eventType = EventType.CAMERA
            val eventTag = when (name) {
                EventType.NOTIFICATION.value -> {
                    eventType = EventType.NOTIFICATION
                    when (type.split(":").first()) {
                        EventTag.WARNING.value -> EventTag.WARNING
                        EventTag.ERROR.value -> EventTag.ERROR
                        else -> EventTag.INFORMATION
                    }
                }
                EventType.CAMERA.value -> EventTag.INFORMATION
                else -> EventTag.INFORMATION
            }
            CameraEvent(
                name = type.split(":").last(),
                date = date,
                eventType = eventType,
                eventTag = eventTag,
                value = value
            )
        }
}
