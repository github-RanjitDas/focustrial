package com.lawmobile.domain.entities

data class DomainInformationImageMetadata(
    val photoMetadata: DomainPhotoMetadata,
    val videosAssociated: List<DomainVideoMetadata>? = null
)
