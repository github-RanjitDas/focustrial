package com.lawmobile.data.datasource.remote.liveStreaming

import com.safefleet.mobile.avml.cameras.external.CameraDataSource
import com.safefleet.mobile.commons.helpers.Result

class LiveStreamingRemoteDataSourceImpl(private val cameraDataSource: CameraDataSource) :
    LiveStreamingRemoteDataSource {
    override fun getUrlForLiveStream(): String {
        return cameraDataSource.getUrlForLiveStream()
    }

    override suspend fun takePhoto(): Result<Unit> {
        return cameraDataSource.takePhoto()
    }

    override suspend fun startRecordVideo(): Result<Unit> {
        return cameraDataSource.startRecordVideo()
    }

    override suspend fun stopRecordVideo(): Result<Unit> {
        return cameraDataSource.stopRecordVideo()
    }
}