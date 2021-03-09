package com.lawmobile.data.datasource.remote.thumbnailList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class ThumbnailListRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    ThumbnailListRemoteDataSource {
    override suspend fun getImageBytes(snapshot: CameraConnectFile): Result<ByteArray> =
        cameraConnectService.getImageBytes(snapshot)

    override suspend fun getSnapshotList(): Result<CameraConnectFileResponseWithErrors> =
        cameraConnectService.getListOfImages()
}