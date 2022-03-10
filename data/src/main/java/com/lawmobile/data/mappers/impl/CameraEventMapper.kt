package com.lawmobile.data.mappers.impl

import com.lawmobile.body_cameras.entities.LogEvent
import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.data.mappers.LocalMapper
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.extensions.simpleDateFormat

object CameraEventMapper :
    DomainMapper<Any, CameraEvent>,
    LocalMapper<CameraEvent, LocalCameraEvent> {

    override fun Any.toDomain(): CameraEvent {
        return when (this) {
            is LogEvent -> toDomain()
            is LocalCameraEvent -> toDomain()
            else -> throw NotImplementedError()
        }
    }

    private fun LocalCameraEvent.toDomain() = CameraEvent(
        name = name,
        eventType = EventType.getByValue(eventType),
        eventTag = EventTag.getByValue(eventTag),
        value = value,
        date = date,
        isRead = isRead == 1L
    )

    private fun LogEvent.toDomain(): CameraEvent {
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
        return CameraEvent(
            name = type.split(":").last(),
            date = date.simpleDateFormat(),
            eventType = eventType,
            eventTag = eventTag,
            value = value
        )
    }

    override fun CameraEvent.toLocal(): LocalCameraEvent =
        LocalCameraEvent(
            name = name,
            eventType = eventType.value,
            eventTag = eventTag.value,
            value = value,
            date = date,
            isRead = if (isRead) 1 else 0
        )

    fun List<Any>.toDomainList(): List<CameraEvent> = map { it.toDomain() }
    fun List<CameraEvent>.toLocalList(): List<LocalCameraEvent> = map { it.toLocal() }
}
