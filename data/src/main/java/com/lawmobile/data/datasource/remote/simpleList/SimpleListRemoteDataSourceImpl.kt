package com.lawmobile.data.datasource.remote.simpleList

import com.lawmobile.data.entities.RemoteVideoMetadata
import com.lawmobile.data.entities.VideoListMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.getResultWithAttempts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class SimpleListRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    SimpleListRemoteDataSource {

    override suspend fun getSnapshotList(): Result<CameraConnectFileResponseWithErrors> =
        cameraConnectService.getListOfImages()

    override suspend fun getVideoList(): Result<CameraConnectFileResponseWithErrors> {
        val response = withContext(Dispatchers.IO) { cameraConnectService.getListOfVideos() }
        if (response is Result.Success) {
            getMetadataForVideoList(response.data.items)
        }
        return response
    }

    private suspend fun getMetadataForVideoList(videos: List<CameraConnectFile>) {
        videos.forEach {
            val metadata = VideoListMetadata.getVideoMetadata(it.name)
            if (metadata == null) {
                delay(100)
                val videoMetadataResponse =
                    getResultWithAttempts(GET_METADATA_ATTEMPTS){
                        cameraConnectService.getVideoMetadata(it.name, it.nameFolder)
                    }
                if (videoMetadataResponse is Result.Success) {
                    VideoListMetadata.saveOrUpdateVideoMetadata(
                        RemoteVideoMetadata(
                            videoMetadataResponse.data,
                            false
                        )
                    )
                }
            }
        }
    }

    companion object {
        private const val GET_METADATA_ATTEMPTS = 5
    }
}