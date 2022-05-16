package com.lawmobile.data.datasource.remote.liveStreaming

import com.lawmobile.body_cameras.entities.CameraCatalog
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class LiveStreamingRemoteDataSourceImpl(cameraServiceFactory: CameraServiceFactory) :
    LiveStreamingRemoteDataSource {

    private var cameraService = cameraServiceFactory.create()

    override fun getUrlForLiveStream(): String = cameraService.getUrlForLiveStream()

    override suspend fun takePhoto(): Result<Unit> = cameraService.takePhoto()

    override suspend fun startRecordVideo(): Result<Unit> = cameraService.startRecordVideo()

    override suspend fun stopRecordVideo(): Result<Unit> = cameraService.stopRecordVideo()

    override suspend fun getCatalogInfo(): Result<List<CameraCatalog>> =
        cameraService.getCatalogInfo()

    override suspend fun getBatteryLevel(): Result<Int> = cameraService.getBatteryLevel()

    override suspend fun getFreeStorage(): Result<String> = cameraService.getFreeStorage()

    override suspend fun getTotalStorage(): Result<String> = cameraService.getTotalStorage()

    override suspend fun disconnectCamera(): Result<Unit> = cameraService.disconnectCamera()

    override suspend fun isFolderOnCamera(folderName: String): Boolean =
        cameraService.isFolderOnCamera(folderName)
}
