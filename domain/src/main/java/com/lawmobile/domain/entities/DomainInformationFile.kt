package com.lawmobile.domain.entities

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata

data class DomainInformationFile(
    val cameraConnectFile: CameraConnectFile,
    val cameraConnectVideoMetadata: CameraConnectVideoMetadata? = null,
    var isChecked: Boolean = false
)