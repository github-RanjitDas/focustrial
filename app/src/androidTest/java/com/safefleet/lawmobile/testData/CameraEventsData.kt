package com.safefleet.lawmobile.testData

import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent

enum class CameraEventsData(val value: MutableList<LogEvent>) {
    DEFAULT(
        mutableListOf(
            LogEvent(
                name = "Camera",
                date = "10/12/2020 19:53:25",
                type = "warn: low battery",
                value = "please charge your camera"
            )
        )
    ),
    LOG_EVENT_LIST(
        mutableListOf(
            LogEvent(
                name = "Notification",
                date = "11/07/2020 19:53:25",
                type = "warn: low battery",
                value = "please charge your camera"
            ),
            LogEvent(
                name = "Notification",
                date = "04/10/2020 19:53:25",
                type = "warn: low battery",
                value = "please charge your camera"
            ),
            LogEvent(
                name = "Notification",
                date = "05/02/2020 19:53:25",
                type = "warn: low battery",
                value = "please charge your camera"
            ),
            LogEvent(
                name = "Notification",
                date = "03/06/2020 19:53:25",
                type = "warn: low battery",
                value = "please charge your camera"
            )
        )
    )
}
