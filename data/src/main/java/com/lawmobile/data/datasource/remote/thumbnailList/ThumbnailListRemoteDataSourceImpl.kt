package com.lawmobile.data.datasource.remote.thumbnailList

import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors

class ThumbnailListRemoteDataSourceImpl(private val cameraService: CameraService) :
    ThumbnailListRemoteDataSource {
    override suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray> =
        cameraService.getImageBytes(cameraFile)

    override suspend fun getSnapshotList(): Result<FileResponseWithErrors> =
        cameraService.getListOfImages()
}