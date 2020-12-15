package com.lawmobile.domain.repository.snapshotDetail

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface SnapshotDetailRepository : BaseRepository {
    suspend fun getImageBytes(domainCameraFile: DomainCameraFile): Result<ByteArray>
    suspend fun saveSnapshotPartnerId(
        domainCameraFile: DomainCameraFile,
        partnerId: String
    ): Result<Unit>

    suspend fun getInformationOfPhoto(domainCameraFile: DomainCameraFile): Result<DomainInformationImageMetadata>
}