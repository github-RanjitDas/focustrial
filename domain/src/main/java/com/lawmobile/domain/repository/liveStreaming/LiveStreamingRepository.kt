package com.lawmobile.domain.repository.liveStreaming

import com.safefleet.mobile.commons.helpers.Result

interface LiveStreamingRepository {
    fun getUrlForLiveStream(): String
    suspend fun startRecordVideo(): Result<Unit>
    suspend fun stopRecordVideo(): Result<Unit>
    suspend fun takePhoto(): Result<Unit>
}