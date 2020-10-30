package com.lawmobile.domain.entities

data class DomainVideoMetadata (
    val fileName: String,
    val metadata: DomainMetadata? = null,
    var nameFolder: String? = null,
    val officerId: String? = null,
    val path: String? = null,
    val associatedPhotos: List<DomainPhotoAssociated>? = null,
    val serialNumber: String? = null
)