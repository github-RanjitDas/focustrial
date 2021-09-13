package com.lawmobile.domain.entities.customEvents

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType

object InternetErrorEvent {
    const val value = "internet_issues"
    const val title = "Internet Connection Issues"
    const val message = "The FMA does not detect an Internet Connection, please check."

    val event = CameraEvent(
        name = value,
        eventTag = EventTag.INTERNET,
        eventType = EventType.NOTIFICATION
    )
}
