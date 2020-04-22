package com.lawmobile.domain.usecase.liveStreaming

import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository

class LiveStreamingUseCaseImpl(private val liveStreamingRepository: LiveStreamingRepository) :
    LiveStreamingUseCase {
    override fun getUrlForLiveStream(): String {
        return liveStreamingRepository.getUrlForLiveStream()
    }
}