package com.lawmobile.domain.usecase.videoPlayback

import com.lawmobile.domain.entity.DomainInformationVideo
import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class VideoPlaybackUseCaseImpl(private val videoPlaybackRepository: VideoPlaybackRepository) :
    VideoPlaybackUseCase {

    override suspend fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile): Result<DomainInformationVideo> =
        videoPlaybackRepository.getInformationResourcesVideo(cameraConnectFile)

    override suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>> =
        videoPlaybackRepository.getCatalogInfo()

}