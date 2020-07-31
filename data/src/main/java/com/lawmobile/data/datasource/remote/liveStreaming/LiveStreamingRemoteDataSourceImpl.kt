package com.lawmobile.data.datasource.remote.liveStreaming

import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class LiveStreamingRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    LiveStreamingRemoteDataSource {
    override fun getUrlForLiveStream(): String =
        cameraConnectService.getUrlForLiveStream()

    override suspend fun takePhoto(): Result<Unit> =
        cameraConnectService.takePhoto()

    override suspend fun startRecordVideo(): Result<Unit> =
        cameraConnectService.startRecordVideo()

    override suspend fun stopRecordVideo(): Result<Unit> =
        cameraConnectService.stopRecordVideo()

    override suspend fun getCatalogInfo(): Result<List<CameraConnectCatalog>> =
        cameraConnectService.getCatalogInfo()

    override suspend fun getBatteryLevel(): Result<Int> =
        cameraConnectService.getBatteryLevel()

    override suspend fun getFreeStorage(): Result<String> =
        cameraConnectService.getFreeStorage()

    override suspend fun getTotalStorage(): Result<String> =
        cameraConnectService.getTotalStorage()
}