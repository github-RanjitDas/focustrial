package com.lawmobile.data.repository.audioDetail

import com.lawmobile.data.datasource.remote.audioDetail.AudioDetailRemoteDataSource
import com.lawmobile.data.mappers.impl.AudioMetadataMapper.toDomain
import com.lawmobile.data.mappers.impl.FileMapper.toCamera
import com.lawmobile.data.mappers.impl.FileMapper.toDomainList
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationAudioMetadata
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.repository.audioDetail.AudioDetailRepository
import com.safefleet.mobile.external_hardware.cameras.entities.AudioInformation
import com.safefleet.mobile.external_hardware.cameras.entities.AudioMetadata
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result

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
        val cameraAudioMetadata = buildAudioInformation(domainCameraFile, partnerId)

        snapshotDetailRemoteDataSource.savePartnerIdAudio(cameraAudioMetadata).run {
            doIfSuccess { return Result.Success(updateAudioMetadataInCache(cameraAudioMetadata)) }
        }

        return Result.Error(Exception(ERROR_ASSOCIATING_PARTNER))
    }

    private fun updateAudioMetadataInCache(cameraAudioMetadata: AudioInformation) {
        val videosAssociated =
            FileList.findAndGetAudioMetadata(cameraAudioMetadata.fileName)?.videosAssociated
        val domainAudioMetadata = cameraAudioMetadata.toDomain()
        val newItemAudio = DomainInformationAudioMetadata(domainAudioMetadata, videosAssociated)
        FileList.updateItemInAudioMetadataList(newItemAudio)
    }

    private fun buildAudioInformation(
        domainCameraFile: DomainCameraFile,
        partnerId: String
    ) = AudioInformation(
        fileName = domainCameraFile.name,
        officerId = CameraInfo.officerId,
        path = domainCameraFile.path,
        x1sn = CameraInfo.serialNumber,
        metadata = AudioMetadata(partnerID = partnerId),
        nameFolder = domainCameraFile.nameFolder
    )

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

    override suspend fun getAssociatedVideos(domainCameraFile: DomainCameraFile): Result<List<DomainCameraFile>> {
        val cameraFile = domainCameraFile.toCamera()
        return when (val result = snapshotDetailRemoteDataSource.getAssociatedVideos(cameraFile)) {
            is Result.Success -> Result.Success(result.data.toDomainList())
            is Result.Error -> result
        }
    }

    companion object {
        private const val ERROR_ASSOCIATING_PARTNER = "Partner ID could not be associated"
        private const val ERROR_GETTING_AUDIO_INFORMATION =
            "Was not possible get information from the camera"
    }
}
