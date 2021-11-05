package com.lawmobile.domain.entities.customEvents

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType

object WrongCredentialsEvent {
    const val value = "hotspot_connection_issues"
    const val title = "Hotspot Connection Issues"
    const val message = "Please check your credentials and verify your Body-Camera's hotspot is on."

    val event = CameraEvent(
        name = value,
        eventTag = EventTag.ERROR,
        eventType = EventType.NOTIFICATION
    )
}
