package com.lawmobile.data.mappers

import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent

object CameraEventMapper {

    fun cameraToDomainList(logEventList: List<LogEvent>) =
        logEventList.map { cameraToDomain(it) }

    private fun cameraToDomain(logEvent: LogEvent) =
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

    fun localToDomainList(events: List<LocalCameraEvent>) =
        events.map { localToDomain(it) }

    private fun localToDomain(event: LocalCameraEvent) = event.run {
        CameraEvent(
            name = name,
            eventType = EventType.getByValue(eventType),
            eventTag = EventTag.getByValue(eventTag),
            value = value,
            date = date,
            isRead = isRead == 1L
        )
    }

    fun domainToLocalList(events: List<CameraEvent>) =
        events.map { domainToLocal(it) }

    private fun domainToLocal(event: CameraEvent) = event.run {
        LocalCameraEvent(
            name = name,
            eventType = eventType.value,
            eventTag = eventTag.value,
            value = value,
            date = date,
            isRead = if (isRead) 1 else 0
        )
    }
}
