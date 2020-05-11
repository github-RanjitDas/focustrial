package com.lawmobile.domain.repository.snapshotDetail

import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface SnapshotDetailRepository: BaseRepository {
    suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray>
}