package com.lawmobile.data.repository.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class SnapshotDetailRepositoryImpl(private val snapshotDetailRemoteDataSource: SnapshotDetailRemoteDataSource) :
    SnapshotDetailRepository {
    override suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray> {
        return snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile)
    }
}