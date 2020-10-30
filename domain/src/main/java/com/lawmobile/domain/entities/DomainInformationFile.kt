package com.lawmobile.domain.entities

data class DomainInformationFile(
    override val domainCameraFile: DomainCameraFile,
    var domainVideoMetadata: DomainVideoMetadata? = null,
    override var isSelected: Boolean = false
) : DomainInformationForList