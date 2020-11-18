package com.lawmobile.data.repository.liveStreaming

import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
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
        val result = liveRemoteDataSource.stopRecordVideo()
        if (result is Result.Success) FileList.changeListOfVideos(emptyList())
        return result
    }

    override suspend fun takePhoto(): Result<Unit> {
        val result = liveRemoteDataSource.takePhoto()
        if (result is Result.Success) FileList.changeListOfImages(emptyList())
        return result
    }

    override suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>> =
        liveRemoteDataSource.getCatalogInfo()
}