package com.lawmobile.body_cameras.entities

data class NotificationDictionary(
    var id: String,
    var name: String,
    val type: String,
    var value: String,
    val note: String? = null
)
