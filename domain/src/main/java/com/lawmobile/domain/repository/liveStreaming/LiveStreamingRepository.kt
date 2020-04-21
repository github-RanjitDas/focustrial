package com.lawmobile.domain.repository.liveStreaming

interface LiveStreamingRepository {
    fun getUrlForLiveStream(): String
}