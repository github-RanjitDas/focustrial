package com.lawmobile.domain.entities

import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata

data class DomainInformationImageMetadata(
    val cameraConnectPhotoMetadata: CameraConnectPhotoMetadata,
    val videosAssociated: List<CameraConnectVideoMetadata>? = null
)