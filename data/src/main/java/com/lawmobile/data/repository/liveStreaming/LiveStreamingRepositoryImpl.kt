package com.lawmobile.data.repository.liveStreaming

import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSource
import com.lawmobile.data.mappers.impl.CatalogMapper.toDomainList
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.entities.MetadataEvent
import com.lawmobile.domain.enums.CatalogTypes
import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.delay

class LiveStreamingRepositoryImpl(
    private val liveRemoteDataSource: LiveStreamingRemoteDataSource
) : LiveStreamingRepository {

    override fun getUrlForLiveStream(): String {
        return liveRemoteDataSource.getUrlForLiveStream()
    }

    override suspend fun startRecordVideo(): Result<Unit> {
        delay(RECORD_OPERATION_TIME)
        return liveRemoteDataSource.startRecordVideo()
    }

    override suspend fun stopRecordVideo(): Result<Unit> {
        delay(RECORD_OPERATION_TIME)
        val result = liveRemoteDataSource.stopRecordVideo()
        if (result is Result.Success) FileList.videoList = emptyList()
        return result
    }

    override suspend fun takePhoto(): Result<Unit> {
        val result = liveRemoteDataSource.takePhoto()
        if (result is Result.Success) FileList.imageList = emptyList()
        return result
    }

    override suspend fun getCatalogInfo(supportedCatalogType: CatalogTypes): Result<List<MetadataEvent>> {
        return when (val result = liveRemoteDataSource.getCatalogInfo(mapCatalogTypeEnum(supportedCatalogType))) {
            is Result.Success -> Result.Success(result.data.toDomainList())
            is Result.Error -> result
        }
    }

    private fun mapCatalogTypeEnum(supportedCatalogType: CatalogTypes): com.lawmobile.body_cameras.enums.CatalogTypesDto {
        return when (supportedCatalogType) {
            CatalogTypes.CATEGORIES -> {
                com.lawmobile.body_cameras.enums.CatalogTypesDto.CATEGORIES
            }

            CatalogTypes.EVENT -> {
                com.lawmobile.body_cameras.enums.CatalogTypesDto.EVENT
            }
        }
    }

    override suspend fun getBatteryLevel(): Result<Int> = liveRemoteDataSource.getBatteryLevel()

    override suspend fun getFreeStorage(): Result<String> = liveRemoteDataSource.getFreeStorage()

    override suspend fun getTotalStorage(): Result<String> = liveRemoteDataSource.getTotalStorage()

    override suspend fun disconnectCamera(): Result<Unit> = liveRemoteDataSource.disconnectCamera()

    override suspend fun isFolderOnCamera(folderName: String): Boolean =
        liveRemoteDataSource.isFolderOnCamera(folderName)

    companion object {
        const val RECORD_OPERATION_TIME = 1000L
    }
}
