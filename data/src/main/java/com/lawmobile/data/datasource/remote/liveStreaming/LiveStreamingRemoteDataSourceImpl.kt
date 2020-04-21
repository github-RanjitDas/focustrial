package com.lawmobile.data.datasource.remote.liveStreaming

import com.safefleet.mobile.avml.cameras.CameraDataSource

class LiveStreamingRemoteDataSourceImpl (private val cameraDataSource: CameraDataSource) : LiveStreamingRemoteDataSource {
    override fun getUrlForLiveStream(): String {
        return cameraDataSource.getUrlForLiveStream()
    }
}