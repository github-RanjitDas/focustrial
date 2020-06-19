package com.lawmobile.domain.usecase.videoPlayback

import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.commons.helpers.Result

class VideoPlaybackUseCaseImpl(private val videoPlaybackRepository: VideoPlaybackRepository) :
    VideoPlaybackUseCase {

    override suspend fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile): Result<DomainInformationVideo> =
        videoPlaybackRepository.getInformationResourcesVideo(cameraConnectFile)

    override suspend fun saveVideoMetadata(cameraConnectVideoMetadata: CameraConnectVideoMetadata): Result<Unit> =
        videoPlaybackRepository.saveVideoMetadata(cameraConnectVideoMetadata)

    override suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ): Result<CameraConnectVideoMetadata> =
        videoPlaybackRepository.getVideoMetadata(fileName, folderName)
}