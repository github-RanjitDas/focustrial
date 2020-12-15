package com.lawmobile.presentation.utils

import com.safefleet.mobile.external_hardware.cameras.CameraService

class CameraHelper(
    private val cameraService: CameraService,
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
        return cameraService.isCameraConnected(wifiHelper.getGatewayAddress())
    }
}