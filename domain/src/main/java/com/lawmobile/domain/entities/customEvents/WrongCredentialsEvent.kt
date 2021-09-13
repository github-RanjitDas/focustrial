package com.lawmobile.domain.entities.customEvents

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType

object WrongCredentialsEvent {
    const val value = "hotspot_connection_issues"
    const val title = "Hotspot Connection Issues"
    const val message = "The officer ID or the device password are not right.\n\nPlease, verify your credentials."

    val event = CameraEvent(
        name = value,
        eventTag = EventTag.ERROR,
        eventType = EventType.NOTIFICATION
    )
}
