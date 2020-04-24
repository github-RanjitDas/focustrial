package com.lawmobile.domain.repository.liveStreaming

import com.safefleet.mobile.commons.helpers.Result

interface LiveStreamingRepository {
    fun getUrlForLiveStream(): String
    suspend fun takePhoto(): Result<Unit>
}