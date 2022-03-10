package com.lawmobile.data.datasource.remote.videoPlayback

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.VideoFileInfo
import com.lawmobile.body_cameras.entities.VideoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface VideoPlaybackRemoteDataSource {
    suspend fun getInformationResourcesVideo(cameraFile: CameraFile): Result<VideoFileInfo>
    suspend fun saveVideoMetadata(videoInformation: VideoInformation): Result<Unit>
    suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ): Result<VideoInformation>
}
