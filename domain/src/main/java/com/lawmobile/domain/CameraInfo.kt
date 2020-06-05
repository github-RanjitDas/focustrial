package com.lawmobile.domain

import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.avml.cameras.external.CameraType

object CameraInfo {
    var cameraType: CameraType = CameraType.X1
    var events = mutableListOf<CameraConnectCatalog>()
    var officerId = ""
    var serialNumber = ""
}