package com.lawmobile.domain.entities.customEvents

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.utils.DateHelper

object BluetoothErrorEvent {
    const val value = "bluetooth_issues"
    const val title = "The mobile Bluetooth is off"
    const val message = "Please turn on the mobile Bluetooth"

    val event = CameraEvent(
        name = value,
        eventTag = EventTag.BLUETOOTH,
        eventType = EventType.NOTIFICATION,
        date = DateHelper.getCurrentDate()
    )
}
