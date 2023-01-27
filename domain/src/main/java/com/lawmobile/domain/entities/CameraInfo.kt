package com.lawmobile.domain.entities

import com.lawmobile.domain.enums.BackOfficeType
import com.lawmobile.domain.enums.CameraType

object CameraInfo {
    var cameraType: CameraType = CameraType.X1
    var backOfficeType: BackOfficeType = BackOfficeType.COMMAND_CENTRE
    var discoveryUrl = ""
    var tenantId = ""
    var metadataEvents = mutableListOf<MetadataEvent>()
    var isOfficerLogged: Boolean = false
    var officerId = ""
    var serialNumber = ""
    var officerName = ""
    var areNewChanges = false
    var currentNotificationCount = 0
    var isBluetoothEnable = false
    var isCovertModeEnable = false
    lateinit var videoDetailMetaDataCached: VideoDetailMetaDataCached

    fun cleanInfo() {
        metadataEvents = mutableListOf()
        isOfficerLogged = false
        officerId = ""
        serialNumber = ""
        officerName = ""
        areNewChanges = false
        currentNotificationCount = 0
    }

    fun setCamera(cameraType: CameraType) {
        this.cameraType = cameraType
    }
}
