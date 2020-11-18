package com.lawmobile.domain.usecase.liveStreaming

import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.commons.helpers.Result

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

    override suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>> =
        liveStreamingRepository.getCatalogInfo()
}