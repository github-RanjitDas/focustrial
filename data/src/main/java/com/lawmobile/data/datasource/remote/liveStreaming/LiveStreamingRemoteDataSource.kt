package com.lawmobile.data.datasource.remote.liveStreaming

import com.safefleet.mobile.commons.helpers.Result

interface LiveStreamingRemoteDataSource {
    fun getUrlForLiveStream(): String
    suspend fun startRecordVideo(): Result<Unit>
    suspend fun stopRecordVideo(): Result<Unit>
    suspend fun takePhoto(): Result<Unit>
}