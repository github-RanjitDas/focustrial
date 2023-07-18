package com.lawmobile.domain.entities.customEvents

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.utils.DateHelper

object ConnectionErrorEvent {
    const val value = "connection_issues"
    const val title = "Something went wrong"
    const val message = "Please try again"

    val event = CameraEvent(
        name = value,
        eventTag = EventTag.ERROR,
        eventType = EventType.NOTIFICATION,
        date = DateHelper.getCurrentDate()
    )
}
