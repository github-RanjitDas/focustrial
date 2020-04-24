package com.lawmobile.domain.usecase.liveStreaming

import com.safefleet.mobile.commons.helpers.Result

interface LiveStreamingUseCase {
    fun getUrlForLiveStream(): String
    suspend fun startRecordVideo(): Result<Unit>
    suspend fun stopRecordVideo(): Result<Unit>
    suspend fun takePhoto(): Result<Unit>
}