package com.lawmobile.domain.enums

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
        "Your BWC will stop running in 7 minutes. Please charge your BWC"
    ) {
        override fun getTypeOfEvent(): EventType {
            return EventType.NOTIFICATION
        }
    },
    LOW_STORAGE(
        "low_storage_warning",
        "Low Storage Warning!",
        "Your BWC is critically low on storage"
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
        "Your BWC lost its GPS signal"
    ) {
        override fun getTypeOfEvent(): EventType {
            return EventType.NOTIFICATION
        }
    },
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
                UNKNOWN_OPERATION.value -> UNKNOWN_OPERATION
                else -> DEFAULT
            }
        }
    }
}
