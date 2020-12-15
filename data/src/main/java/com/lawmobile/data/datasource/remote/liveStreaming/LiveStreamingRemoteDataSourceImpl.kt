package com.lawmobile.data.datasource.remote.liveStreaming

import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraCatalog

class LiveStreamingRemoteDataSourceImpl(private val cameraService: CameraService) :
    LiveStreamingRemoteDataSource {
    override fun getUrlForLiveStream(): String =
        cameraService.getUrlForLiveStream()

    override suspend fun takePhoto(): Result<Unit> =
        cameraService.takePhoto()

    override suspend fun startRecordVideo(): Result<Unit> =
        cameraService.startRecordVideo()

    override suspend fun stopRecordVideo(): Result<Unit> =
        cameraService.stopRecordVideo()

    override suspend fun getCatalogInfo(): Result<List<CameraCatalog>> =
        cameraService.getCatalogInfo()

    override suspend fun getBatteryLevel(): Result<Int> =
        cameraService.getBatteryLevel()

    override suspend fun getFreeStorage(): Result<String> =
        cameraService.getFreeStorage()

    override suspend fun getTotalStorage(): Result<String> =
        cameraService.getTotalStorage()

    override suspend fun disconnectCamera(): Result<Unit> =
        cameraService.disconnectCamera()
}