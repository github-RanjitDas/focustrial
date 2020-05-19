package com.lawmobile.data.repository.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.data.mappers.MapperCameraConnectVideoInfoDomainVideo
import com.lawmobile.domain.entity.DomainInformationVideo
import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class VideoPlaybackRepositoryImpl(private val videoPlaybackRemoteDataSource: VideoPlaybackRemoteDataSource) :
    VideoPlaybackRepository {

    override suspend fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile): Result<DomainInformationVideo> {
        return when (val response =
            videoPlaybackRemoteDataSource.getInformationResourcesVideo(cameraConnectFile)) {
            is Result.Success -> Result.Success(
                MapperCameraConnectVideoInfoDomainVideo.cameraConnectFileToDomainInformationVideo(
                    response.data
                )
            )
            is Result.Error -> response
        }
    }

    override suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>> =
        videoPlaybackRemoteDataSource.getCatalogInfo()

}