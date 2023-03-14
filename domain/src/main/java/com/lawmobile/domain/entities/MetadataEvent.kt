package com.lawmobile.domain.entities

data class MetadataEvent(
    var id: String,
    var name: String,
    var type: String?,
    var order: Int = 0
)
