package com.lawmobile.data.repository.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.data.entities.RemoteCameraMetadata
import com.lawmobile.data.mappers.MapperCameraConnectVideoInfoDomainVideo
import com.lawmobile.domain.entity.DomainInformationVideo
import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.commons.helpers.Result

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
        val metadata =
            metadataList.find { it.videoMetadata.fileName == cameraConnectVideoMetadata.fileName }
        val result = videoPlaybackRemoteDataSource.saveVideoMetadata(cameraConnectVideoMetadata)
        if (result is Result.Success && metadata != null) {
            val index =
                metadataList.indexOfFirst { it.videoMetadata.fileName == cameraConnectVideoMetadata.fileName }
            metadata.isChanged = true
            metadataList[index] = metadata
        }
        return result
    }

    override suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ): Result<CameraConnectVideoMetadata> {
        val cameraConnectVideoMetadata = metadataList.find { it.videoMetadata.fileName == fileName }
        return if (cameraConnectVideoMetadata != null && !cameraConnectVideoMetadata.isChanged) {
            Result.Success(cameraConnectVideoMetadata.videoMetadata)
        } else {
            val result = videoPlaybackRemoteDataSource.getVideoMetadata(fileName, folderName)
            if (result is Result.Success) {
                val metadata = RemoteCameraMetadata(result.data, false)
                if (cameraConnectVideoMetadata != null) {
                    val index = metadataList.indexOfFirst { it.videoMetadata.fileName == fileName }
                    metadataList[index] = metadata
                } else {
                    metadataList.add(metadata)
                }
            }
            result
        }
    }

    companion object {
        var metadataList = mutableListOf<RemoteCameraMetadata>()
        const val ERROR_TO_GET_VIDEO = "An error has occurred, try to open the video again"
    }
}