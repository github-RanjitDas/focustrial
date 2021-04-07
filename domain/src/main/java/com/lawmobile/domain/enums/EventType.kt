package com.lawmobile.domain.enums

enum class EventType(val value: String) {
    NOTIFICATION("Notification"),
    CAMERA("Camera"),
    DIAGNOSIS("Diagnosis");

    companion object {
        fun getByValue(value: String): EventType {
            return when (value) {
                NOTIFICATION.value -> NOTIFICATION
                CAMERA.value -> CAMERA
                DIAGNOSIS.value -> DIAGNOSIS
                else -> throw Exception("Event type not supported or does not exist")
            }
        }
    }
}
