package com.lawmobile.domain.usecase.snapshotDetail

import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface SnapshotDetailUseCase : BaseUseCase {
    suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray>
}