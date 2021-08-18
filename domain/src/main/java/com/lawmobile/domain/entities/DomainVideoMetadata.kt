package com.lawmobile.domain.entities

data class DomainVideoMetadata(
    val fileName: String,
    val metadata: DomainMetadata? = null,
    var nameFolder: String? = null,
    val officerId: String? = null,
    val path: String? = null,
    val associatedFiles: List<DomainAssociatedFile>? = null,
    val serialNumber: String? = null,
    var endTime: String? = null,
    var gmtOffset: String? = null,
    var hash: DomainHashVideo? = null,
    var preEvent: String? = null,
    var startTime: String? = null,
    var videoSpecs: String? = null
)
