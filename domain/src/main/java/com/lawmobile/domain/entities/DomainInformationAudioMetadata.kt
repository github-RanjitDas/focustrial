package com.lawmobile.domain.entities

data class DomainInformationAudioMetadata(
    val audioMetadata: DomainAudioMetadata,
    val videosAssociated: List<DomainVideoMetadata>? = null
)
