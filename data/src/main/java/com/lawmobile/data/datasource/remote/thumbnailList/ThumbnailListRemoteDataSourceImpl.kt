package com.lawmobile.data.datasource.remote.thumbnailList

import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ThumbnailListRemoteDataSourceImpl(cameraServiceFactory: CameraServiceFactory) :
    ThumbnailListRemoteDataSource {

    private var cameraService = cameraServiceFactory.create()

    override suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray> =
        cameraService.getImageBytes(cameraFile)

    override suspend fun getSnapshotList(): Result<FileResponseWithErrors> =
        cameraService.getListOfImages()
}
