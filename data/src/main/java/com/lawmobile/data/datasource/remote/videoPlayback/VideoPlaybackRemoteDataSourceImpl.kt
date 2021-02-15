package com.lawmobile.data.datasource.remote.videoPlayback

import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.VideoFileInfo
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result

class VideoPlaybackRemoteDataSourceImpl(private val cameraService: CameraService) :
    VideoPlaybackRemoteDataSource {

    override suspend fun getInformationResourcesVideo(cameraFile: CameraFile): Result<VideoFileInfo> =
        cameraService.getInformationResourcesVideo(cameraFile)

    override suspend fun saveVideoMetadata(videoInformation: VideoInformation): Result<Unit> =
        cameraService.saveVideoMetadata(videoInformation)

    override suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ): Result<VideoInformation> = cameraService.getVideoMetadata(fileName, folderName)
}
