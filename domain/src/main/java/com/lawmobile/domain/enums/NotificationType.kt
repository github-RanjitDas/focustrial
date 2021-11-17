package com.lawmobile.domain.enums

import com.lawmobile.domain.entities.customEvents.BluetoothErrorEvent
import com.lawmobile.domain.entities.customEvents.InternetErrorEvent
import com.lawmobile.domain.entities.customEvents.WrongCredentialsEvent

enum class NotificationType(
    val value: String,
    val title: String? = null,
    val message: String? = null
) {

    BATTERY_LEVEL("battery_level") {
        override fun getTypeOfEvent(): EventType {
            return EventType.CAMERA
        }
    },
    STORAGE_REMAIN("storage_remain") {
        override fun getTypeOfEvent(): EventType {
            return EventType.CAMERA
        }
    },
    DIAGNOSIS_COMPLETED("self-test completed") {
        override fun getTypeOfEvent(): EventType {
            return EventType.DIAGNOSIS
        }
    },
    DIAGNOSIS("self-test") {
        override fun getTypeOfEvent(): EventType {
            return EventType.DIAGNOSIS
        }
    },
    LOW_BATTERY(
        "low_battery_warning",
        "Low Battery Warning!",
        ""
    ) {
        override fun getTypeOfEvent(): EventType {
            return EventType.NOTIFICATION
        }

        override fun getCustomMessage(value: String?): String {
            return "Your Body-Camera will stop running in ${value ?: 7} minutes. Please charge your Body-Camera"
        }
    },
    LOW_STORAGE(
        "low_storage_warning",
        "Low Storage Warning!",
        "Your Body-Camera is critically low on storage"
    ) {
        override fun getTypeOfEvent(): EventType {
            return EventType.NOTIFICATION
        }
    },
    VIDEO_RECORD_COMPLETE("video_record_complete") {
        override fun getTypeOfEvent(): EventType {
            return EventType.CAMERA
        }
    },
    STARTING_VIDEO_RECORD("starting_video_record") {
        override fun getTypeOfEvent(): EventType {
            return EventType.CAMERA
        }
    },
    GPS_SIGNAL_LOST(
        "GPS_signal_lost",
        "GPS Signal Lost!",
        "Your Body-Camera lost its GPS signal"
    ) {
        override fun getTypeOfEvent(): EventType {
            return EventType.NOTIFICATION
        }
    },
    INTERNET_CONNECTION_ISSUES(
        InternetErrorEvent.value,
        InternetErrorEvent.title,
        InternetErrorEvent.message
    ),
    BLUETOOTH_CONNECTION_ISSUES(
        BluetoothErrorEvent.value,
        BluetoothErrorEvent.title,
        BluetoothErrorEvent.message
    ),
    HOTSPOT_CONNECTION_ISSUES(
        WrongCredentialsEvent.value,
        WrongCredentialsEvent.title,
        WrongCredentialsEvent.message
    ),
    UNKNOWN_OPERATION(
        "unknown_operation",
        "Unknown operation",
        "An unknown error occurred!"
    ) {
        override fun getTypeOfEvent(): EventType {
            return EventType.NOTIFICATION
        }
    },
    DEFAULT("") {
        override fun getTypeOfEvent(): EventType {
            return EventType.CAMERA
        }
    };

    open fun getTypeOfEvent(): EventType {
        return EventType.NOTIFICATION
    }

    open fun getCustomMessage(value: String? = null): String? {
        return message
    }

    companion object {
        fun getByValue(value: String): NotificationType {
            return when (value) {
                BATTERY_LEVEL.value -> BATTERY_LEVEL
                STORAGE_REMAIN.value -> STORAGE_REMAIN
                DIAGNOSIS_COMPLETED.value -> DIAGNOSIS_COMPLETED
                DIAGNOSIS.value -> DIAGNOSIS
                LOW_BATTERY.value -> LOW_BATTERY
                LOW_STORAGE.value -> LOW_STORAGE
                VIDEO_RECORD_COMPLETE.value -> VIDEO_RECORD_COMPLETE
                STARTING_VIDEO_RECORD.value -> STARTING_VIDEO_RECORD
                GPS_SIGNAL_LOST.value -> GPS_SIGNAL_LOST
                INTERNET_CONNECTION_ISSUES.value -> INTERNET_CONNECTION_ISSUES
                BLUETOOTH_CONNECTION_ISSUES.value -> BLUETOOTH_CONNECTION_ISSUES
                HOTSPOT_CONNECTION_ISSUES.value -> HOTSPOT_CONNECTION_ISSUES
                UNKNOWN_OPERATION.value -> UNKNOWN_OPERATION
                else -> DEFAULT
            }
        }
    }
}
