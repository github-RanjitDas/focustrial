package com.lawmobile.domain.usecase.snapshotDetail

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.commons.helpers.Result

interface SnapshotDetailUseCase : BaseUseCase {
    suspend fun getImageBytes(domainCameraFile: DomainCameraFile): Result<ByteArray>
    suspend fun savePartnerIdSnapshot(
        domainCameraFile: DomainCameraFile,
        partnerId: String
    ): Result<Unit>

    suspend fun getInformationOfPhoto(domainCameraFile: DomainCameraFile): Result<DomainInformationImageMetadata>
}