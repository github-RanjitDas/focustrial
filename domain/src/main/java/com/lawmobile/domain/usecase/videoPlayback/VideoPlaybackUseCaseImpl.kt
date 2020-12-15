package com.lawmobile.domain.usecase.videoPlayback

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class VideoPlaybackUseCaseImpl(private val videoPlaybackRepository: VideoPlaybackRepository) :
    VideoPlaybackUseCase {

    override suspend fun getInformationResourcesVideo(domainCameraFile: DomainCameraFile): Result<DomainInformationVideo> =
        videoPlaybackRepository.getInformationResourcesVideo(domainCameraFile)

    override suspend fun saveVideoMetadata(domainVideoMetadata: DomainVideoMetadata): Result<Unit> =
        videoPlaybackRepository.saveVideoMetadata(domainVideoMetadata)

    override suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ) = videoPlaybackRepository.getVideoMetadata(fileName, folderName)

}