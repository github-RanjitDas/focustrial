package com.lawmobile.domain.usecase.audioDetail

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.repository.audioDetail.AudioDetailRepository

class AudioDetailUseCaseImpl(private val audioDetailRepository: AudioDetailRepository) :
    AudioDetailUseCase {
    override suspend fun getAudioBytes(domainCameraFile: DomainCameraFile) =
        audioDetailRepository.getAudioBytes(domainCameraFile)

    override suspend fun savePartnerIdAudio(
        domainCameraFile: DomainCameraFile,
        partnerId: String
    ) = audioDetailRepository.saveAudioPartnerId(domainCameraFile, partnerId)

    override suspend fun getInformationOfAudio(domainCameraFile: DomainCameraFile) =
        audioDetailRepository.getInformationOfAudio(domainCameraFile)

    override suspend fun getAssociatedVideos(domainCameraFile: DomainCameraFile) =
        audioDetailRepository.getAssociatedVideos(domainCameraFile)
}
