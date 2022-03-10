package com.lawmobile.data.datasource.remote.videoPlayback

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.VideoFileInfo
import com.lawmobile.body_cameras.entities.VideoInformation
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class VideoPlaybackRemoteDataSourceImpl(cameraServiceFactory: CameraServiceFactory) :
    VideoPlaybackRemoteDataSource {

    private var cameraService = cameraServiceFactory.create()

    override suspend fun getInformationResourcesVideo(cameraFile: CameraFile): Result<VideoFileInfo> =
        cameraService.getInformationResourcesVideo(cameraFile)

    override suspend fun saveVideoMetadata(videoInformation: VideoInformation): Result<Unit> =
        cameraService.saveVideoMetadata(videoInformation)

    override suspend fun getVideoMetadata(
        fileName: String,
        folderName: String
    ): Result<VideoInformation> = cameraService.getVideoMetadata(fileName, folderName)
}
