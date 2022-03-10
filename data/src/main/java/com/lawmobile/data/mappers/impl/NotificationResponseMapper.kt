package com.lawmobile.data.mappers.impl

import com.lawmobile.body_cameras.entities.NotificationResponse
import com.lawmobile.data.mappers.DomainMapper
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.NotificationType
import com.lawmobile.domain.utils.DateHelper

object NotificationResponseMapper : DomainMapper<NotificationResponse, CameraEvent> {
    override fun NotificationResponse.toDomain(): CameraEvent {
        val eventName = type.split(":").last()
        val eventType = NotificationType.getByValue(eventName).getTypeOfEvent()
        val eventTag = when (type.split(":").first()) {
            EventTag.WARNING.value -> EventTag.WARNING
            EventTag.ERROR.value -> EventTag.ERROR
            else -> EventTag.INFORMATION
        }

        return CameraEvent(
            name = eventName,
            eventType = eventType,
            eventTag = eventTag,
            value = param,
            date = DateHelper.getCurrentDate(),
            isRead = true
        )
    }
}
