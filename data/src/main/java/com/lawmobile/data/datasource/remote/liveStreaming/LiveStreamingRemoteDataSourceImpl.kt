package com.lawmobile.data.datasource.remote.liveStreaming

import com.safefleet.mobile.avml.cameras.CameraDataSource
import com.safefleet.mobile.commons.helpers.Result

class LiveStreamingRemoteDataSourceImpl (private val cameraDataSource: CameraDataSource) : LiveStreamingRemoteDataSource {
    override fun getUrlForLiveStream(): String {
        return cameraDataSource.getUrlForLiveStream()
    }

    override suspend fun takePhoto(): Result<Unit> {
        return cameraDataSource.takePhoto()
    }
}