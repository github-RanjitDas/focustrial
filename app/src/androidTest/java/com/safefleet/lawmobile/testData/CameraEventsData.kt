package com.safefleet.lawmobile.testData

import com.lawmobile.body_cameras.entities.LogEvent

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
                date = "2022/01/19 19:53:25",
                type = "warn: low battery",
                value = "please charge your camera"
            ),
            LogEvent(
                name = "Notification",
                date = "2022/01/19 19:53:50",
                type = "warn: low battery",
                value = "please charge your camera"
            ),
            LogEvent(
                name = "Notification",
                date = "2022/01/19 19:53:35",
                type = "warn: low battery",
                value = "please charge your camera"
            ),
            LogEvent(
                name = "Notification",
                date = "2022/01/19 19:53:45",
                type = "warn: low battery",
                value = "please charge your camera"
            )
        )
    )
}
