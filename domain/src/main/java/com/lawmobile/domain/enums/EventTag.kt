package com.lawmobile.domain.enums

enum class EventTag(val value: String) {
    INFORMATION(""),
    WARNING("warn"),
    ERROR("err"),
    INTERNET("internet"),
    BLUETOOTH("bluetooth");

    companion object {
        fun getByValue(value: String): EventTag {
            return when (value) {
                INFORMATION.value -> INFORMATION
                WARNING.value -> WARNING
                ERROR.value -> ERROR
                INTERNET.value -> INTERNET
                BLUETOOTH.value -> BLUETOOTH
                else -> throw Exception("Event tag not supported or does not exist")
            }
        }
    }
}
