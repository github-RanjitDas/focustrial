package com.lawmobile.domain.usecase.audioDetail

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationAudioMetadata
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface AudioDetailUseCase : BaseUseCase {
    suspend fun getAudioBytes(domainCameraFile: DomainCameraFile): Result<ByteArray>
    suspend fun savePartnerIdAudio(
        domainCameraFile: DomainCameraFile,
        partnerId: String
    ): Result<Unit>

    suspend fun getInformationOfAudio(domainCameraFile: DomainCameraFile): Result<DomainInformationAudioMetadata>
}
