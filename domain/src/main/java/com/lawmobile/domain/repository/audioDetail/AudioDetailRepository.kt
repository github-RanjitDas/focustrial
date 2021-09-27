package com.lawmobile.domain.repository.audioDetail

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationAudioMetadata
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface AudioDetailRepository : BaseRepository {
    suspend fun getAudioBytes(domainCameraFile: DomainCameraFile): Result<ByteArray>
    suspend fun saveAudioPartnerId(
        domainCameraFile: DomainCameraFile,
        partnerId: String
    ): Result<Unit>

    suspend fun getInformationOfAudio(domainCameraFile: DomainCameraFile): Result<DomainInformationAudioMetadata>
}
