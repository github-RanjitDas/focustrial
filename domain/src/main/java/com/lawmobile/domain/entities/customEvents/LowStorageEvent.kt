package com.lawmobile.domain.entities.customEvents

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.enums.NotificationType
import com.lawmobile.domain.utils.DateHelper

object LowStorageEvent {
    val value = NotificationType.LOW_STORAGE.value
    val title = NotificationType.LOW_STORAGE.title
    val message = NotificationType.LOW_STORAGE.message

    val event = CameraEvent(
        name = value,
        eventTag = EventTag.INFORMATION,
        eventType = EventType.NOTIFICATION,
        date = DateHelper.getCurrentDate()
    )
}
