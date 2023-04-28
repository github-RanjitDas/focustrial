package com.lawmobile.domain.utils

import com.lawmobile.domain.entities.CameraEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ConnectionHelper {
    fun isCameraConnected(ipAddress: String): Boolean
    fun isCameraConnectedV2(ipAddress: String): Boolean
    fun onCameraEvent(callback: (CameraEvent) -> Unit)
    suspend fun disconnectCamera(): Result<Unit>
    fun reviewIfArriveNotificationInCMDSocket()
    suspend fun resetViewFinder(ipAddressClient: String): Boolean
}
