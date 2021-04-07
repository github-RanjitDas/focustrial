package com.lawmobile.domain.enums

enum class EventType(val value: String) {
    NOTIFICATION("Notification"),
    CAMERA("Camera");

    companion object {
        fun getByValue(value: String): EventType {
            return when (value) {
                "Notification" -> NOTIFICATION
                "Camera" -> CAMERA
                else -> throw Exception("Event type not supported or does not exist")
            }
        }
    }
}
