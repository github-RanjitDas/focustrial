package com.lawmobile.domain.usecase.liveStreaming

import com.lawmobile.domain.entities.MetadataEvent
import com.lawmobile.domain.enums.CatalogTypes
import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class LiveStreamingUseCaseImpl(private val liveStreamingRepository: LiveStreamingRepository) :
    LiveStreamingUseCase {
    override fun getUrlForLiveStream(): String {
        return liveStreamingRepository.getUrlForLiveStream()
    }

    override suspend fun startRecordVideo(): Result<Unit> {
        return liveStreamingRepository.startRecordVideo()
    }

    override suspend fun stopRecordVideo(): Result<Unit> {
        return liveStreamingRepository.stopRecordVideo()
    }

    override suspend fun takePhoto(): Result<Unit> {
        return liveStreamingRepository.takePhoto()
    }

    override suspend fun getCatalogInfo(supportedCatalogType: CatalogTypes): Result<List<MetadataEvent>> =
        liveStreamingRepository.getCatalogInfo(supportedCatalogType)

    override suspend fun getBatteryLevel(): Result<Int> =
        liveStreamingRepository.getBatteryLevel()

    override suspend fun getFreeStorage(): Result<String> =
        liveStreamingRepository.getFreeStorage()

    override suspend fun getTotalStorage(): Result<String> =
        liveStreamingRepository.getTotalStorage()

    override suspend fun disconnectCamera(): Result<Unit> =
        liveStreamingRepository.disconnectCamera()

    override suspend fun isFolderOnCamera(folderName: String): Boolean =
        liveStreamingRepository.isFolderOnCamera(folderName)
}
