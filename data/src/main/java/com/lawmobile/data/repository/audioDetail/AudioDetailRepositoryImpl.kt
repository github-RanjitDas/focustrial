package com.lawmobile.data.repository.audioDetail

import com.lawmobile.data.datasource.remote.audioDetail.AudioDetailRemoteDataSource
import com.lawmobile.data.mappers.impl.AudioMetadataMapper.toDomain
import com.lawmobile.data.mappers.impl.FileMapper.toCamera
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationAudioMetadata
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.repository.audioDetail.AudioDetailRepository
import com.safefleet.mobile.external_hardware.cameras.entities.AudioInformation
import com.safefleet.mobile.external_hardware.cameras.entities.AudioMetadata
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.delay

class AudioDetailRepositoryImpl(
    private val snapshotDetailRemoteDataSource: AudioDetailRemoteDataSource
) : AudioDetailRepository {

    override suspend fun getAudioBytes(domainCameraFile: DomainCameraFile): Result<ByteArray> {
        val cameraConnectFile = domainCameraFile.toCamera()
        return snapshotDetailRemoteDataSource.getAudioBytes(cameraConnectFile)
    }

    override suspend fun saveAudioPartnerId(
        domainCameraFile: DomainCameraFile,
        partnerId: String
    ): Result<Unit> {
        val partnerMetadata = AudioMetadata(partnerID = partnerId)
        val cameraAudioMetadata = AudioInformation(
            fileName = domainCameraFile.name,
            officerId = CameraInfo.officerId,
            path = domainCameraFile.path,
            x1sn = CameraInfo.serialNumber,
            metadata = partnerMetadata,
            nameFolder = domainCameraFile.nameFolder
        )

        delay(DELAY_TO_SAVE)

        with(snapshotDetailRemoteDataSource.savePartnerIdAudio(cameraAudioMetadata)) {
            doIfSuccess {
                val item = FileList.findAndGetAudioMetadata(domainCameraFile.name)
                val domainAudioMetadata = cameraAudioMetadata.toDomain()
                val newItemAudio =
                    DomainInformationAudioMetadata(domainAudioMetadata, item?.videosAssociated)
                FileList.updateItemInAudioMetadataList(newItemAudio)
                return Result.Success(Unit)
            }
        }
        return Result.Error(Exception(ERROR_ASSOCIATING_PARTNER))
    }

    override suspend fun getInformationOfAudio(domainCameraFile: DomainCameraFile): Result<DomainInformationAudioMetadata> {
        val item = FileList.findAndGetAudioMetadata(domainCameraFile.name)
        if (item != null) return Result.Success(item)

        val cameraConnectFile = domainCameraFile.toCamera()

        with(snapshotDetailRemoteDataSource.getInformationOfAudio(cameraConnectFile)) {
            doIfSuccess {
                val domainAudioMetadata = it.toDomain()
                val domainInformationAudio =
                    DomainInformationAudioMetadata(domainAudioMetadata, emptyList())

                FileList.updateItemInAudioMetadataList(domainInformationAudio)
                return Result.Success(domainInformationAudio)
            }
        }

        return Result.Error(Exception(ERROR_GETTING_AUDIO_INFORMATION))
    }

    companion object {
        private const val ERROR_ASSOCIATING_PARTNER = "Partner ID could not be associated"
        private const val ERROR_GETTING_AUDIO_INFORMATION =
            "Was not possible get information from the camera"
        private const val DELAY_TO_SAVE = 150L
    }
}
