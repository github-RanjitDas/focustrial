package com.lawmobile.data.datasource.remote.fileList

import com.lawmobile.data.entities.RemoteVideoMetadata
import com.lawmobile.data.entities.VideoListMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class FileListRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    FileListRemoteDataSource {

    override suspend fun getSnapshotList(): Result<CameraConnectFileResponseWithErrors> =
        cameraConnectService.getListOfImages()

    override suspend fun getVideoList(): Result<CameraConnectFileResponseWithErrors> {
        val response = withContext(Dispatchers.Default) { cameraConnectService.getListOfVideos() }
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
                    cameraConnectService.getVideoMetadata(it.name, it.nameFolder)
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

    override suspend fun savePartnerIdVideos(cameraConnectVideoMetadata: CameraConnectVideoMetadata): Result<Unit> =
        cameraConnectService.saveVideoMetadata(cameraConnectVideoMetadata)

    override suspend fun savePartnerIdSnapshot(cameraConnectPhotoMetadata: CameraConnectPhotoMetadata): Result<Unit> =
        cameraConnectService.savePhotoMetadata(cameraConnectPhotoMetadata)
}