package com.lawmobile.domain.entities

import com.lawmobile.domain.enums.CameraType

object CameraInfo {
    var cameraType: CameraType = CameraType.X1
    var events = mutableListOf<DomainCatalog>()
    var officerId = ""
    var serialNumber = ""
    var officerName = ""
    var areNewChanges = false

    fun cleanInfo() {
        events = mutableListOf()
        officerId = ""
        serialNumber = ""
        officerName = ""
        areNewChanges = false
    }

    fun setCameraType(serialNumber: String) {
        cameraType = CameraType.getTypeOfCamera(serialNumber)
    }
}
