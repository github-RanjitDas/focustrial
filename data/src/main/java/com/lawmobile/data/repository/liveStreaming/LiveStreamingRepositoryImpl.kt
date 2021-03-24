package com.lawmobile.data.repository.liveStreaming

import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSource
import com.lawmobile.data.mappers.CatalogMapper
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.entities.MetadataEvent
import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.delay

class LiveStreamingRepositoryImpl(private val liveRemoteDataSource: LiveStreamingRemoteDataSource) :
    LiveStreamingRepository {
    override fun getUrlForLiveStream(): String {
        return liveRemoteDataSource.getUrlForLiveStream()
    }

    override suspend fun startRecordVideo(): Result<Unit> {
        delay(1000)
        return liveRemoteDataSource.startRecordVideo()
    }

    override suspend fun stopRecordVideo(): Result<Unit> {
        delay(1000)
        val result = liveRemoteDataSource.stopRecordVideo()
        if (result is Result.Success) FileList.changeVideoList(emptyList())
        return result
    }

    override suspend fun takePhoto(): Result<Unit> {
        val result = liveRemoteDataSource.takePhoto()
        if (result is Result.Success) FileList.changeImageList(emptyList())
        return result
    }

    override suspend fun getCatalogInfo(): Result<List<MetadataEvent>> =
        when (val result = liveRemoteDataSource.getCatalogInfo()) {
            is Result.Success -> Result.Success(CatalogMapper.cameraToDomainList(result.data))
            is Result.Error -> result
        }

    override suspend fun getBatteryLevel(): Result<Int> =
        liveRemoteDataSource.getBatteryLevel()

    override suspend fun getFreeStorage(): Result<String> =
        liveRemoteDataSource.getFreeStorage()

    override suspend fun getTotalStorage(): Result<String> =
        liveRemoteDataSource.getTotalStorage()

    override suspend fun disconnectCamera(): Result<Unit> =
        liveRemoteDataSource.disconnectCamera()
}
