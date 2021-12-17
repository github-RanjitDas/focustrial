package com.lawmobile.domain.entities.customEvents

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType

object LoginRequestErrorEvent {
    const val value = "online_login_error"
    const val title = "Something Went Wrong"
    const val message = "Please use your device password to login or contact your administrator."

    val event = CameraEvent(
        name = value,
        eventTag = EventTag.ERROR,
        eventType = EventType.NOTIFICATION
    )
}
