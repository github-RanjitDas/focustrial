package com.lawmobile.data.datasource.remote.videoPlayback

import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.VideoFileInfo
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation

interface VideoPlaybackRemoteDataSource {
    suspend fun getInformationResourcesVideo(cameraFile: CameraFile): Result<VideoFileInfo>
    suspend fun saveVideoMetadata(videoInformation: VideoInformation): Result<Unit>
    suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ): Result<VideoInformation>
}