package com.lawmobile.domain.usecase.snapshotDetail

import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class SnapshotDetailUseCaseImpl(private val snapshotDetailRepository: SnapshotDetailRepository) :
    SnapshotDetailUseCase {
    override suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray> {
        return snapshotDetailRepository.getImageBytes(cameraConnectFile)
    }

    override suspend fun savePartnerIdSnapshot(
        cameraFile: CameraConnectFile,
        partnerId: String
    ): Result<Unit> {
        return snapshotDetailRepository.saveSnapshotPartnerId(cameraFile, partnerId)
    }

    override suspend fun getInformationOfPhoto(cameraFile: CameraConnectFile): Result<DomainInformationImageMetadata> {
        return snapshotDetailRepository.getInformationOfPhoto(cameraFile)
    }
}