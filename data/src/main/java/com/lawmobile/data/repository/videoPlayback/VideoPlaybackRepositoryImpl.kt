package com.lawmobile.data.repository.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.data.entities.RemoteVideoMetadata
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.data.mappers.MapperCameraConnectVideoInfoDomainVideo
import com.lawmobile.domain.entity.DomainInformationVideo
import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.delay

class VideoPlaybackRepositoryImpl(private val videoPlaybackRemoteDataSource: VideoPlaybackRemoteDataSource) :
    VideoPlaybackRepository {

    override suspend fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile): Result<DomainInformationVideo> {
        return when (val response =
            videoPlaybackRemoteDataSource.getInformationResourcesVideo(cameraConnectFile)) {
            is Result.Success -> {
                return try {
                    Result.Success(
                        MapperCameraConnectVideoInfoDomainVideo.cameraConnectFileToDomainInformationVideo(
                            response.data
                        )
                    )
                } catch (e: Exception) {
                    Result.Error(Exception(ERROR_TO_GET_VIDEO))
                }
            }
            is Result.Error -> response
        }
    }

    override suspend fun saveVideoMetadata(cameraConnectVideoMetadata: CameraConnectVideoMetadata): Result<Unit> {
        val result = videoPlaybackRemoteDataSource.saveVideoMetadata(cameraConnectVideoMetadata)
        if (result is Result.Success) {
            val metadata = RemoteVideoMetadata(cameraConnectVideoMetadata, true)
            VideoListMetadata.saveOrUpdateVideoMetadata(metadata)
        }
        return result
    }

    override suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ): Result<CameraConnectVideoMetadata> {

        val cameraConnectVideoMetadata = VideoListMetadata.getVideoMetadata(fileName)
        return if (cameraConnectVideoMetadata != null && !cameraConnectVideoMetadata.isChanged) {
            Result.Success(cameraConnectVideoMetadata.videoMetadata)
        } else {
            delay(100)
            val result = videoPlaybackRemoteDataSource.getVideoMetadata(fileName, folderName)
            if (result is Result.Success) {
                val metadata = RemoteVideoMetadata(result.data, false)
                VideoListMetadata.saveOrUpdateVideoMetadata(metadata)
            }
            result
        }
    }

    companion object {
        const val ERROR_TO_GET_VIDEO = "An error has occurred, try to open the video again"
    }
}