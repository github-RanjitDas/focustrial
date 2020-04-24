package com.lawmobile.domain.usecase.liveStreaming

import com.safefleet.mobile.commons.helpers.Result

interface LiveStreamingUseCase {
    fun getUrlForLiveStream(): String
    suspend fun takePhoto(): Result<Unit>
}