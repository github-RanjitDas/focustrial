package com.lawmobile.data.repository.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.domain.entities.RemoteVideoMetadata
import com.lawmobile.domain.entities.VideoListMetadata
import com.lawmobile.data.mappers.FileMapper
import com.lawmobile.data.mappers.VideoInformationMapper
import com.lawmobile.data.mappers.VideoMetadataMapper
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class VideoPlaybackRepositoryImpl(private val videoPlaybackRemoteDataSource: VideoPlaybackRemoteDataSource) :
    VideoPlaybackRepository {

    override suspend fun getInformationResourcesVideo(domainCameraFile: DomainCameraFile): Result<DomainInformationVideo> {
        val cameraConnectFile = FileMapper.domainToCamera(domainCameraFile)
        return when (val response =
            videoPlaybackRemoteDataSource.getInformationResourcesVideo(cameraConnectFile)) {
            is Result.Success -> {
                return try {
                    Result.Success(VideoInformationMapper.cameraToDomain(response.data))
                } catch (e: Exception) {
                    Result.Error(Exception(ERROR_TO_GET_VIDEO))
                }
            }
            is Result.Error -> response
        }
    }

    override suspend fun saveVideoMetadata(domainVideoMetadata: DomainVideoMetadata): Result<Unit> {
        val cameraConnectVideoMetadata = VideoMetadataMapper.domainToCamera(domainVideoMetadata)
        val result = videoPlaybackRemoteDataSource.saveVideoMetadata(cameraConnectVideoMetadata)
        if (result is Result.Success) {
            val metadata = RemoteVideoMetadata(domainVideoMetadata, true)
            VideoListMetadata.saveOrUpdateVideoMetadata(metadata)
        }
        return result
    }

    override suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ): Result<DomainVideoMetadata> {
        val cameraConnectVideoMetadata = VideoListMetadata.getVideoMetadata(fileName)
        return if (cameraConnectVideoMetadata != null && !cameraConnectVideoMetadata.isChanged) {
            Result.Success(cameraConnectVideoMetadata.videoMetadata)
        } else {
            return when (val result =
                videoPlaybackRemoteDataSource.getVideoMetadata(fileName, folderName)) {
                is Result.Success -> {
                    val domainVideoMetadata = VideoMetadataMapper.cameraToDomain(result.data)
                    val metadata = RemoteVideoMetadata(domainVideoMetadata, false)
                    VideoListMetadata.saveOrUpdateVideoMetadata(metadata)
                    Result.Success(VideoMetadataMapper.cameraToDomain(result.data))
                }
                is Result.Error -> result
            }
        }
    }

    companion object {
        const val ERROR_TO_GET_VIDEO = "An error has occurred, try to open the video again"
    }
}