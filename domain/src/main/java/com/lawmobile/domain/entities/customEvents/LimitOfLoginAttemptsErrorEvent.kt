package com.lawmobile.domain.entities.customEvents

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.utils.DateHelper

object LimitOfLoginAttemptsErrorEvent {
    const val value = "limit_login_attempts_error"
    const val title = "Limit of Login Attempts"
    const val subTitle = "FMA will be closed due to 5 invalid login attempts."
    const val message = "Please contact your administrator, reopen the app and try to login again."

    val event = CameraEvent(
        name = value,
        eventTag = EventTag.ERROR,
        eventType = EventType.NOTIFICATION,
        date = DateHelper.getCurrentDate()
    )
}
