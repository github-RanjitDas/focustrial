package com.lawmobile.domain.usecase.snapshotDetail

import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class SnapshotDetailUseCaseImpl(private val snapshotDetailRepository: SnapshotDetailRepository): SnapshotDetailUseCase {
    override suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray> {
        return snapshotDetailRepository.getImageBytes(cameraConnectFile)
    }
}