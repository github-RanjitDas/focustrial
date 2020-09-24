package com.lawmobile.domain.entities

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile

interface DomainInformationForList {
    val cameraConnectFile: CameraConnectFile
    var isSelected: Boolean
}