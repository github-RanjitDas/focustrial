package com.lawmobile.domain.entities

import com.lawmobile.domain.NotificationDictionary
import com.lawmobile.domain.enums.BackOfficeType
import com.lawmobile.domain.enums.CameraType

object CameraInfo {
    var cameraType: CameraType = CameraType.X1
    var defaultPasswordForCC = "1234567890"
    var backOfficeType: BackOfficeType = BackOfficeType.COMMAND_CENTRE
    var discoveryUrl = ""
    var tenantId = ""
    var userId = ""
    var wifiApRouterMode = 1
    var notificationDictionaryList: List<NotificationDictionary> = ArrayList()
    var metadataEvents = mutableListOf<MetadataEvent>()
    var isOfficerLogged: Boolean = false
    var isCameraConnected: Boolean = false
    var officerId = ""
    var deviceIdFromConfig = ""
    var serialNumber = ""
    var officerName = ""
    var areNewChanges = false
    var currentNotificationCount = 0
    var isBluetoothEnable = false
    var isCovertModeEnable = false
    lateinit var videoDetailMetaDataCached: VideoDetailMetaDataCached
    var fragmentListTypeToLoadForSnapshot: String = "thumbnailFileList"
    var fragmentListTypeToLoadForVideo: String = "simpleFileList"
    var playbackPosition: Long? = 0
    const val BATTERY_TOTAL_HOURS = 10f
    const val TOTAL_PERCENTAGE = 100

    fun cleanInfo() {
        metadataEvents = mutableListOf()
        isOfficerLogged = false
        isCameraConnected = false
        officerId = ""
        serialNumber = ""
        officerName = ""
        areNewChanges = false
        currentNotificationCount = 0
    }

    fun getDescriptiveTextFromNotificationDictionary(value: String?): String? {
        notificationDictionaryList.forEach {
            if (it.type.equals(value, true)) {
                return it.note
            }
        }
        return ""
    }

    fun setCamera(cameraType: CameraType) {
        this.cameraType = cameraType
    }

    fun isBackOfficeCC(): Boolean {
        return backOfficeType != BackOfficeType.NEXUS
    }
}
