package com.lawmobile.domain.enums

enum class EventTag(val value: String) {
    INFORMATION(""),
    WARNING("warn"),
    ERROR("err");

    companion object {
        fun getByValue(value: String): EventTag {
            return when (value) {
                "" -> INFORMATION
                "warn" -> WARNING
                "err" -> ERROR
                else -> throw Exception("Event tag not supported or does not exist")
            }
        }
    }
}
