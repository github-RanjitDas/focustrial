package com.lawmobile.data.dao.entities

data class LocalCameraEvent(
    val id: Long,
    val name: String,
    val eventType: String,
    val eventTag: String,
    val value: String?,
    val date: String,
    val isRead: Long
)
