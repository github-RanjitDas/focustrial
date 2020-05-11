package com.lawmobile.data.datasource.remote.snapshotDetail

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class SnapshotDetailRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    SnapshotDetailRemoteDataSource {
    override suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray> {
        return cameraConnectService.getImageBytes(cameraConnectFile)
    }
}