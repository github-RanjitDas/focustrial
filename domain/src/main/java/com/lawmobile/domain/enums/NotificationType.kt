package com.lawmobile.domain.enums

import com.lawmobile.domain.entities.customEvents.BluetoothErrorEvent
import com.lawmobile.domain.entities.customEvents.IncorrectPasswordErrorEvent
import com.lawmobile.domain.entities.customEvents.InternetErrorEvent
import com.lawmobile.domain.entities.customEvents.LimitOfLoginAttemptsErrorEvent
import com.lawmobile.domain.entities.customEvents.LoginRequestErrorEvent
import com.lawmobile.domain.entities.customEvents.PermissionDeniedErrorEvent
import com.lawmobile.domain.entities.customEvents.WrongCredentialsEvent

enum class NotificationType(
    val value: String,
    val title: String? = null,
    val message: String? = null,
    val subTitle: String? = null,
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
        "Low Battery",
        ""
    ) {
        override fun getTypeOfEvent(): EventType {
            return EventType.NOTIFICATION
        }

        override fun getCustomMessage(value: String?): String {
            return "Your Body Camera will stop running in ${value ?: 7} minutes. Please charge your Body Camera"
        }
    },
    LOW_STORAGE(
        "low_storage_warning",
        "Low Storage",
        "Your Body Camera is critically low on storage"
    ) {
        override fun getTypeOfEvent(): EventType {
            return EventType.NOTIFICATION
        }
    },
    VIDEO_RECORDING_STOPPED("video_record_complete") {
        override fun getTypeOfEvent(): EventType {
            return EventType.CAMERA
        }
    },
    VIDEO_RECORDING_STARTED("starting_video_record") {
        override fun getTypeOfEvent(): EventType {
            return EventType.CAMERA
        }
    },
    GPS_SIGNAL_LOST(
        "GPS_signal_lost",
        "GPS Signal Lost!",
        "Your Body Camera lost its GPS signal"
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
    LOGIN_REQUEST_ERROR(
        LoginRequestErrorEvent.value,
        LoginRequestErrorEvent.title,
        LoginRequestErrorEvent.message
    ),
    INCORRECT_PASSWORD_ERROR(
        IncorrectPasswordErrorEvent.value,
        IncorrectPasswordErrorEvent.title,
        IncorrectPasswordErrorEvent.message
    ),
    LIMIT_OF_LOGIN_ATTEMPTS_ERROR(
        LimitOfLoginAttemptsErrorEvent.value,
        LimitOfLoginAttemptsErrorEvent.title,
        LimitOfLoginAttemptsErrorEvent.message,
        LimitOfLoginAttemptsErrorEvent.subTitle,
    ),
    PERMISSIONS_DENIED_ERROR(
        PermissionDeniedErrorEvent.value,
        PermissionDeniedErrorEvent.title,
        PermissionDeniedErrorEvent.message
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
                VIDEO_RECORDING_STARTED.value -> VIDEO_RECORDING_STARTED
                VIDEO_RECORDING_STOPPED.value -> VIDEO_RECORDING_STOPPED
                GPS_SIGNAL_LOST.value -> GPS_SIGNAL_LOST
                INTERNET_CONNECTION_ISSUES.value -> INTERNET_CONNECTION_ISSUES
                BLUETOOTH_CONNECTION_ISSUES.value -> BLUETOOTH_CONNECTION_ISSUES
                HOTSPOT_CONNECTION_ISSUES.value -> HOTSPOT_CONNECTION_ISSUES
                INCORRECT_PASSWORD_ERROR.value -> INCORRECT_PASSWORD_ERROR
                LIMIT_OF_LOGIN_ATTEMPTS_ERROR.value -> LIMIT_OF_LOGIN_ATTEMPTS_ERROR
                LOGIN_REQUEST_ERROR.value -> LOGIN_REQUEST_ERROR
                UNKNOWN_OPERATION.value -> UNKNOWN_OPERATION
                PERMISSIONS_DENIED_ERROR.value -> PERMISSIONS_DENIED_ERROR
                else -> DEFAULT
            }
        }
    }
}
