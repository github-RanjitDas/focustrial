package com.lawmobile.domain.entities.customEvents

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.utils.DateHelper

object PermissionDeniedErrorEvent {
    const val value = "permission_error_denied_error"
    const val title = "Location Permission"
    const val message = "You have denied required permissions for app to work and the app will be closed"

    val event = CameraEvent(
        name = value,
        eventTag = EventTag.ERROR,
        eventType = EventType.NOTIFICATION,
        date = DateHelper.getCurrentDate()
    )
}
