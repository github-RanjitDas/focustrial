package com.lawmobile.domain.enums

enum class NotificationType(val value: String) {

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
    LOW_BATTERY("low_battery_warning") {
        override fun getTypeOfEvent(): EventType {
            return EventType.NOTIFICATION
        }
    },
    LOW_STORAGE("low_storage_warning") {
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
                else -> DEFAULT
            }
        }
    }
}
