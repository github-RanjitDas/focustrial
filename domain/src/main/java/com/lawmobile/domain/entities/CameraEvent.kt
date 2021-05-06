package com.lawmobile.domain.entities

import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType

data class CameraEvent(
    val name: String,
    val eventType: EventType,
    val eventTag: EventTag,
    val value: String? = null,
    var date: String = "",
    var isRead: Boolean = false
)
