package com.lawmobile.domain.entities

object CameraInfo {
    var cameraType: CameraType = CameraType.X1
    var events = mutableListOf<DomainCatalog>()
    var officerId = ""
    var serialNumber = ""
    var officerName = ""
    var areNewChanges = false
}