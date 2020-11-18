package com.lawmobile.data.datasource.remote.liveStreaming

import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class LiveStreamingRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    LiveStreamingRemoteDataSource {
    override fun getUrlForLiveStream(): String {
        return cameraConnectService.getUrlForLiveStream()
    }

    override suspend fun takePhoto(): Result<Unit> {
        return cameraConnectService.takePhoto()
    }

    override suspend fun startRecordVideo(): Result<Unit> {
        return cameraConnectService.startRecordVideo()
    }

    override suspend fun stopRecordVideo(): Result<Unit> {
        return cameraConnectService.stopRecordVideo()
    }

    override suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>> =
        cameraConnectService.getCatalogInfo()
}