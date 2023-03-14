package com.lawmobile.domain.entities.customEvents

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.utils.DateHelper

object IncorrectPasswordErrorEvent {
    const val value = "incorrect_password_error"
    const val title = "Incorrect Password"
    const val message = "The password you entered is incorrect. Please try again."

    val event = CameraEvent(
        name = value,
        eventTag = EventTag.ERROR,
        eventType = EventType.NOTIFICATION,
        date = DateHelper.getCurrentDate()
    )
}
