package com.lawmobile.presentation.utils

import com.safefleet.mobile.avml.cameras.external.CameraConnectService

class CameraHelper(
    private val cameraConnectService: CameraConnectService,
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
        return cameraConnectService.isCameraConnected(wifiHelper.getGatewayAddress())
    }
}