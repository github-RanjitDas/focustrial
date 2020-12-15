package com.lawmobile.data.datasource.remote.liveStreaming

import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.external_hardware.cameras.entities.CameraCatalog

interface LiveStreamingRemoteDataSource {
    fun getUrlForLiveStream(): String
    suspend fun startRecordVideo(): Result<Unit>
    suspend fun stopRecordVideo(): Result<Unit>
    suspend fun takePhoto(): Result<Unit>
    suspend fun getCatalogInfo(): Result<List<CameraCatalog>>
    suspend fun getBatteryLevel(): Result<Int>
    suspend fun getFreeStorage(): Result<String>
    suspend fun getTotalStorage(): Result<String>
    suspend fun disconnectCamera(): Result<Unit>
}