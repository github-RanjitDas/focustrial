package com.lawmobile.domain.usecase.snapshotDetail

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.repository.snapshotDetail.SnapshotDetailRepository

class SnapshotDetailUseCaseImpl(private val snapshotDetailRepository: SnapshotDetailRepository) :
    SnapshotDetailUseCase {
    override suspend fun getImageBytes(domainCameraFile: DomainCameraFile) =
        snapshotDetailRepository.getImageBytes(domainCameraFile)

    override suspend fun savePartnerIdSnapshot(
        domainCameraFile: DomainCameraFile,
        partnerId: String
    ) = snapshotDetailRepository.saveSnapshotPartnerId(domainCameraFile, partnerId)

    override suspend fun getInformationOfPhoto(domainCameraFile: DomainCameraFile) =
        snapshotDetailRepository.getInformationOfPhoto(domainCameraFile)
}