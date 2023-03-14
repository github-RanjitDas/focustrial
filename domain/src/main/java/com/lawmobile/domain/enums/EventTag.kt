package com.lawmobile.domain.enums

enum class EventTag(val value: String) {
    NOTIFICATION("notification"),
    INFORMATION("information"),
    WARNING("warning"),
    ERROR("error"),
    INTERNET("internet"),
    BLUETOOTH("bluetooth");

    companion object {
        fun getByValue(value: String): EventTag {
            return when {
                value.contains(NOTIFICATION.value, true) -> NOTIFICATION
                value.contains(INFORMATION.value, true) -> INFORMATION
                value.contains(WARNING.value, true) -> WARNING
                value.contains(ERROR.value, true) -> ERROR
                value.contains(INTERNET.value, true) -> INTERNET
                value.contains(BLUETOOTH.value, true) -> BLUETOOTH
                else -> NOTIFICATION
            }
        }
    }
}
