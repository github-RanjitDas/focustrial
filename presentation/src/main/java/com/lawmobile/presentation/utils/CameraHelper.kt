package com.lawmobile.presentation.utils

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.utils.ConnectionHelper

class CameraHelper(
    private val connectionHelper: ConnectionHelper,
    private val wifiHelper: WifiHelper
) {

    companion object {
        private lateinit var cameraHelper: CameraHelper

        fun setInstance(cameraHelper: CameraHelper) {
            this.cameraHelper = cameraHelper
        }

        fun getInstance() = cameraHelper
    }

    fun checkWithAlertIfTheCameraIsConnected(): Boolean {
        return connectionHelper.isCameraConnected(wifiHelper.getGatewayAddress())
    }

    fun reviewNotificationInCamera(notificationCallback: (CameraEvent) -> Unit) {
        connectionHelper.reviewNotificationInCamera(notificationCallback = notificationCallback)
    }
}
