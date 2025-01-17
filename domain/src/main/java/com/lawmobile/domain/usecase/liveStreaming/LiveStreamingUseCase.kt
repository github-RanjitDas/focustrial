package com.lawmobile.domain.usecase.liveStreaming

import com.lawmobile.domain.entities.MetadataEvent
import com.lawmobile.domain.enums.CatalogTypes
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface LiveStreamingUseCase : BaseUseCase {
    fun getUrlForLiveStream(): String
    suspend fun startRecordVideo(): Result<Unit>
    suspend fun stopRecordVideo(): Result<Unit>
    suspend fun takePhoto(): Result<Unit>
    suspend fun getCatalogInfo(supportedCatalogType: CatalogTypes): Result<List<MetadataEvent>>
    suspend fun getBatteryLevel(): Result<Int>
    suspend fun getFreeStorage(): Result<String>
    suspend fun getTotalStorage(): Result<String>
    suspend fun disconnectCamera(): Result<Unit>
    suspend fun isFolderOnCamera(folderName: String): Boolean
}
