package com.lawmobile.domain.entities

data class DomainPhotoMetadata (
    val fileName: String,
    val metadata: DomainMetadata? = null,
    val nameFolder: String? = null,
    val officerId: String? = null,
    val path: String? = null,
    val serialNumber: String? = null
)