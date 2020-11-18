package com.lawmobile.data.datasource.remote.snapshotDetail

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface SnapshotDetailRemoteDataSource {
    suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray>
}