package com.lawmobile.domain.entities

import com.lawmobile.domain.enums.NotificationType

data class DomainNotification(
    val name: String,
    val type: NotificationType,
    val value: String? = null,
    var isRead: Boolean = true,
    var date: String = ""
)
