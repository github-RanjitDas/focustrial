package com.lawmobile.domain.entities

data class DomainNotification(
    val type: NotificationType,
    val value: String? = null,
    var isRead: Boolean = true,
    var date: String = ""
)
