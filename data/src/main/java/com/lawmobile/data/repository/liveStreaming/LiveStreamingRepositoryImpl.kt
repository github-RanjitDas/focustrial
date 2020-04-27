package com.lawmobile.data.repository.liveStreaming

import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSource
import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.safefleet.mobile.commons.helpers.Result

class LiveStreamingRepositoryImpl(private val liveRemoteDataSource: LiveStreamingRemoteDataSource) :
    LiveStreamingRepository {
    override fun getUrlForLiveStream(): String {
        return liveRemoteDataSource.getUrlForLiveStream()
    }

    override suspend fun startRecordVideo(): Result<Unit> {
        return liveRemoteDataSource.startRecordVideo()
    }

    override suspend fun stopRecordVideo(): Result<Unit> {
        return liveRemoteDataSource.stopRecordVideo()
    }

    override suspend fun takePhoto(): Result<Unit> {
        return liveRemoteDataSource.takePhoto()
    }
}