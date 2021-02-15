package com.lawmobile.domain.entities

data class DomainInformationImageMetadata(
    val cameraConnectPhotoMetadata: DomainPhotoMetadata,
    val videosAssociated: List<DomainVideoMetadata>? = null
)
