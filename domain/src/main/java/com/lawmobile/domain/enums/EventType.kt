package com.lawmobile.domain.enums

enum class EventType(val value: String) {
    NOTIFICATION("Notification"),
    CAMERA("Camera"),
    DIAGNOSIS("Diagnosis");

    companion object {
        fun getByValue(value: String): EventType {
            val eventType = when {
                value.contains(NOTIFICATION.value, true) -> {
                    NOTIFICATION
                }
                value.contains(CAMERA.value, true) -> {
                    CAMERA
                }
                value.contains(DIAGNOSIS.value, true) -> {
                    DIAGNOSIS
                }
                else -> {
                    CAMERA
                }
            }
            return eventType
        }
    }
}
