package com.lawmobile.presentation.utils

import com.safefleet.mobile.avml.cameras.x1.CameraDataSource

class CameraHelper(
    private val cameraDataSource: CameraDataSource,
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
        return cameraDataSource.isCameraConnected(wifiHelper.getGatewayAddress())
    }
}