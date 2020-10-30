package com.lawmobile.data.datasource.remote.liveStreaming

import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.commons.helpers.Result

interface LiveStreamingRemoteDataSource {
    fun getUrlForLiveStream(): String
    suspend fun startRecordVideo(): Result<Unit>
    suspend fun stopRecordVideo(): Result<Unit>
    suspend fun takePhoto(): Result<Unit>
    suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>>
    suspend fun getBatteryLevel(): Result<Int>
    suspend fun getFreeStorage(): Result<String>
    suspend fun getTotalStorage(): Result<String>
    suspend fun disconnectCamera(): Result<Unit>
}