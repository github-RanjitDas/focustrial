package com.lawmobile.domain

import com.safefleet.mobile.avml.cameras.external.CameraType

object CameraInfo {
    var cameraType: CameraType = CameraType.X1
    var events = mutableListOf<String>()
    var officerId = ""
    var cameraSerialNumber = ""
}