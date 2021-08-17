package com.lawmobile.data.datasource.remote.simpleList

import com.lawmobile.data.mappers.VideoMetadataMapper
import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.entities.RemoteVideoMetadata
import com.lawmobile.domain.entities.VideoListMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SimpleListRemoteDataSourceImpl(
    cameraServiceFactory: CameraServiceFactory
) : SimpleListRemoteDataSource {

    private var cameraConnectService = cameraServiceFactory.create()

    override suspend fun getSnapshotList(): Result<FileResponseWithErrors> =
        cameraConnectService.getListOfImages()

    override suspend fun getVideoList(): Result<FileResponseWithErrors> {
        val response = withContext(Dispatchers.IO) { cameraConnectService.getListOfVideos() }
        if (response is Result.Success) getMetadataForVideoList(response.data.items)
        return response
    }

    private suspend fun getMetadataForVideoList(videos: List<CameraFile>) {
        videos.forEach {
            VideoListMetadata.getVideoMetadata(it.name) ?: run {
                delay(100)
                val videoMetadataResponse = getResultWithAttempts(GET_METADATA_ATTEMPTS) {
                    cameraConnectService.getVideoMetadata(it.name, it.nameFolder)
                }
                if (videoMetadataResponse is Result.Success) {
                    val domainMetadata =
                        VideoMetadataMapper.cameraToDomain(videoMetadataResponse.data)
                    val remoteMetadata = RemoteVideoMetadata(domainMetadata, false)
                    VideoListMetadata.saveOrUpdateVideoMetadata(remoteMetadata)
                }
            }
        }
    }

    companion object {
        private const val GET_METADATA_ATTEMPTS = 5
    }
}
