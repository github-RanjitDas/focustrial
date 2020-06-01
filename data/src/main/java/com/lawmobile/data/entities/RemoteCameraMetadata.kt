package com.lawmobile.data.entities

import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata

data class RemoteCameraMetadata(
    var videoMetadata: CameraConnectVideoMetadata,
    var isChanged: Boolean
)