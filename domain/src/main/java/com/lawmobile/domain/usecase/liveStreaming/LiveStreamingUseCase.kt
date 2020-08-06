package com.lawmobile.domain.usecase.liveStreaming

import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.commons.helpers.Result

interface LiveStreamingUseCase: BaseUseCase {
    fun getUrlForLiveStream(): String
    suspend fun startRecordVideo(): Result<Unit>
    suspend fun stopRecordVideo(): Result<Unit>
    suspend fun takePhoto(): Result<Unit>
    suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>>
    suspend fun getBatteryLevel(): Result<Int>
    suspend fun getFreeStorage(): Result<String>
    suspend fun getTotalStorage(): Result<String>
}