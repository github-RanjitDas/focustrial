package com.lawmobile.data.datasource.remote.videoPlayback


import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoInfo
import com.safefleet.mobile.avml.cameras.external.CameraDataSource
import com.safefleet.mobile.commons.helpers.Result

class VideoPlaybackRemoteDataSourceImpl(private val cameraDataSource: CameraDataSource) :
    VideoPlaybackRemoteDataSource {
    override suspend fun getInformationResourcesVideo(cameraConnectFile: CameraConnectFile): Result<CameraConnectVideoInfo> {
        return cameraDataSource.getInformationResourcesVideo(cameraConnectFile)
    }
}