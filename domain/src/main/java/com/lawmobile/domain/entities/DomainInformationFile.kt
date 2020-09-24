package com.lawmobile.domain.entities

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata

data class DomainInformationFile(
    override val cameraConnectFile: CameraConnectFile,
    val cameraConnectVideoMetadata: CameraConnectVideoMetadata? = null,
    override var isSelected: Boolean = false
): DomainInformationForList