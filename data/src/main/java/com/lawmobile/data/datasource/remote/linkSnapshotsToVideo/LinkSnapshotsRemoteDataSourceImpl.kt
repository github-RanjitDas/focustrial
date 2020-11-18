package com.lawmobile.data.datasource.remote.linkSnapshotsToVideo

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class LinkSnapshotsRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    LinkSnapshotsRemoteDataSource {
    override suspend fun getImageBytes(snapshot: CameraConnectFile): Result<ByteArray> =
        cameraConnectService.getImageBytes(snapshot)

    override suspend fun getSnapshotList(): Result<CameraConnectFileResponseWithErrors> =
        cameraConnectService.getListOfImages()
}