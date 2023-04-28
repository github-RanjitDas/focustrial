package com.lawmobile.presentation.utils

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.utils.ConnectionHelper
import com.lawmobile.presentation.connectivity.WifiHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class CameraHelper(
    private val connectionHelper: ConnectionHelper,
    private val wifiHelper: WifiHelper,
    private val dispatcher: CoroutineDispatcher
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

    fun checkIfTheCameraIsConnected(): Boolean {
        return connectionHelper.isCameraConnectedV2(wifiHelper.getGatewayAddress())
    }
    fun onCameraEvent(callback: (CameraEvent) -> Unit) {
        connectionHelper.onCameraEvent(callback)
    }

    fun disconnectCamera() {
        CoroutineScope(dispatcher).launch {
            connectionHelper.disconnectCamera()
        }
    }

    fun resetViewFinder() {
        CoroutineScope(dispatcher).launch {
            connectionHelper.resetViewFinder(wifiHelper.getIpAddress())
        }
    }
}
