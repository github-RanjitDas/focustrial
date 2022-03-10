package com.lawmobile.body_cameras.entities

data class LogEvent(
    val name: String,
    val date: String,
    val type: String,
    val value: String = "",
    val additionalInformation: Any? = null
) {
    companion object {
        private const val DATE_INDEX = 0
        private const val NAME_INDEX = 1
        private const val TYPE_INDEX = 2
        private const val VALUE_INDEX = 3

        fun fromCSV(csv: String): LogEvent {
            val values = csv.split(",")
            return LogEvent(
                date = values[DATE_INDEX],
                name = values[NAME_INDEX],
                type = values[TYPE_INDEX],
                value = values[VALUE_INDEX]
            )
        }
    }
}
