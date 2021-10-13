package com.lawmobile.presentation.utils

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.utils.ConnectionHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CameraHelper(
    private val connectionHelper: ConnectionHelper,
    private val wifiHelper: WifiHelper,
    private val backgroundDispatcher: CoroutineDispatcher
) {

    companion object {
        private lateinit var cameraHelper: CameraHelper

        fun setInstance(cameraHelper: CameraHelper) {
            this.cameraHelper = cameraHelper
            this.cameraHelper.connectionHelper.reviewIfArriveNotificationInCMDSocket()
        }

        fun getInstance() = cameraHelper
    }

    fun checkWithAlertIfTheCameraIsConnected(): Boolean {
        return connectionHelper.isCameraConnected(wifiHelper.getGatewayAddress())
    }

    fun onCameraEvent(callback: (CameraEvent) -> Unit) {
        connectionHelper.onCameraEvent(callback)
    }

    fun disconnectCamera() {
        CoroutineScope(backgroundDispatcher).launch {
            connectionHelper.disconnectCamera()
        }
    }
}
