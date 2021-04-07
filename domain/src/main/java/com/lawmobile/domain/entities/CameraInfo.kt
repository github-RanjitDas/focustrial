package com.lawmobile.domain.entities

import com.lawmobile.domain.enums.CameraType

object CameraInfo {
    var cameraType: CameraType = CameraType.X1
    var metadataEvents = mutableListOf<MetadataEvent>()
    var isOfficerLogged: Boolean = false
    var officerId = ""
    var serialNumber = ""
    var officerName = ""
    var areNewChanges = false

    fun cleanInfo() {
        metadataEvents = mutableListOf()
        isOfficerLogged = false
        officerId = ""
        serialNumber = ""
        officerName = ""
        areNewChanges = false
    }

    fun setCameraType(serialNumber: String) {
        cameraType = CameraType.getTypeOfCamera(serialNumber)
    }
}
