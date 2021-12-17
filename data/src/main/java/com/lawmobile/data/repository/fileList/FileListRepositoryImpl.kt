package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.extensions.getDateDependingOnNameLength
import com.lawmobile.data.mappers.impl.AudioMetadataMapper.toDomain
import com.lawmobile.data.mappers.impl.FileMapper.toCameraList
import com.lawmobile.data.mappers.impl.PhotoMetadataMapper.toDomain
import com.lawmobile.data.mappers.impl.VideoMetadataMapper.toCamera
import com.lawmobile.data.mappers.impl.VideoMetadataMapper.toDomain
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationAudioMetadata
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.entities.RemoteVideoMetadata
import com.lawmobile.domain.entities.VideoListMetadata
import com.lawmobile.domain.repository.fileList.FileListRepository
import com.safefleet.mobile.external_hardware.cameras.entities.AudioInformation
import com.safefleet.mobile.external_hardware.cameras.entities.AudioMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.VideoMetadata
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import kotlinx.coroutines.delay

class FileListRepositoryImpl(private val fileListRemoteDataSource: FileListRemoteDataSource) :
    FileListRepository {

    override suspend fun savePartnerIdVideos(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit> {
        val errorsInFiles = associateToVideosAndGetErrors(domainFileList, partnerID)
        return if (errorsInFiles.isEmpty()) Result.Success(Unit)
        else Result.Error(Exception("Partner ID could not be associated to: $errorsInFiles"))
    }

    private suspend fun associateToVideosAndGetErrors(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): MutableList<String> {
        val cameraConnectFileList = domainFileList.toCameraList()
        val errorsInFiles = mutableListOf<String>()

        cameraConnectFileList.forEach {
            val videoInformation = getVideoInformationFromCache(it, partnerID)

            delay(ASSOCIATE_VIDEOS_DELAY)

            val result = getResultWithAttempts(ASSOCIATE_PARTNER_ATTEMPTS) {
                fileListRemoteDataSource.savePartnerIdVideos(videoInformation)
            }

            if (result is Result.Error) errorsInFiles.add(it.getDateDependingOnNameLength())
            else updateVideoMetadataInCache(videoInformation)
        }

        return errorsInFiles
    }

    private fun getVideoInformationFromCache(
        it: CameraFile,
        partnerID: String
    ): VideoInformation {
        val videoInformation: VideoInformation?
        val cachedVideoMetadata = VideoListMetadata.getVideoMetadata(it.name)?.videoMetadata

        if (cachedVideoMetadata?.metadata != null) {
            cachedVideoMetadata.metadata!!.partnerID = partnerID
            cachedVideoMetadata.nameFolder = it.nameFolder
            videoInformation = cachedVideoMetadata.toCamera()
        } else {
            val partnerMetadata = VideoMetadata(partnerID = partnerID)
            videoInformation = buildVideoInformation(it, cachedVideoMetadata, partnerMetadata)
        }
        return videoInformation
    }

    private fun buildVideoInformation(
        it: CameraFile,
        videoMetadata: DomainVideoMetadata?,
        partnerMetadata: VideoMetadata
    ) = VideoInformation(
        fileName = it.name,
        officerId = CameraInfo.officerId,
        x1sn = CameraInfo.serialNumber,
        path = videoMetadata?.path ?: it.path,
        metadata = partnerMetadata,
        nameFolder = it.nameFolder
    )

    private fun updateVideoMetadataInCache(videoInformation: VideoInformation) {
        val domainMetadata = videoInformation.toDomain()
        val remoteMetadata = RemoteVideoMetadata(domainMetadata, true)
        VideoListMetadata.saveOrUpdateVideoMetadata(remoteMetadata)
    }

    override suspend fun savePartnerIdSnapshot(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit> {
        val photoMetadataResult = fileListRemoteDataSource.getSavedPhotosMetadata()
        if (photoMetadataResult is Result.Error) return photoMetadataResult

        val photoMetadataList = (photoMetadataResult as Result.Success).data.toMutableList()

        val errorsInFiles =
            associateToSnapshotsAndGetErrors(domainFileList, photoMetadataList, partnerID)

        delay(ASSOCIATE_ALL_SNAPSHOTS_DELAY)

        val resultJSONOnly = getResultWithAttempts(ASSOCIATE_PARTNER_ATTEMPTS) {
            fileListRemoteDataSource.savePartnerIdInAllSnapshots(photoMetadataList)
        }

        resultJSONOnly.doIfSuccess {
            return if (errorsInFiles.isEmpty()) Result.Success(Unit)
            else Result.Error(Exception("Partner ID could not be associated to: $errorsInFiles"))
        }

        return Result.Error(Exception(ASSOCIATE_PARTNER_ERROR))
    }

    private suspend fun associateToSnapshotsAndGetErrors(
        domainFileList: List<DomainCameraFile>,
        photoMetadataList: MutableList<PhotoInformation>,
        partnerID: String
    ): MutableList<String> {
        val errorsInFiles = mutableListOf<String>()
        val cameraConnectFileList = domainFileList.toCameraList()

        cameraConnectFileList.forEach { fileItem ->
            val partnerMetadata = PhotoMetadata(partnerID = partnerID)
            val cameraPhotoMetadata = buildPhotoInformation(fileItem, partnerMetadata)

            delay(ASSOCIATE_SNAPSHOTS_DELAY)

            val saveResult = getResultWithAttempts(ASSOCIATE_PARTNER_ATTEMPTS) {
                fileListRemoteDataSource.savePartnerIdSnapshot(cameraPhotoMetadata)
            }

            photoMetadataList.removeAll { it.fileName == fileItem.name }
            photoMetadataList.add(cameraPhotoMetadata)

            if (saveResult is Result.Error) {
                errorsInFiles.add(fileItem.getDateDependingOnNameLength())
            } else updateSnapshotMetadataInCache(fileItem, cameraPhotoMetadata)
        }

        return errorsInFiles
    }

    private fun buildPhotoInformation(
        fileItem: CameraFile,
        partnerMetadata: PhotoMetadata
    ) = PhotoInformation(
        fileName = fileItem.name,
        officerId = CameraInfo.officerId,
        path = fileItem.path,
        x1sn = CameraInfo.serialNumber,
        metadata = partnerMetadata,
        nameFolder = fileItem.nameFolder
    )

    private fun updateSnapshotMetadataInCache(
        fileItem: CameraFile,
        cameraPhotoMetadata: PhotoInformation
    ) {
        val item = FileList.findAndGetImageMetadata(fileItem.name)
        val metadata = DomainInformationImageMetadata(
            cameraPhotoMetadata.toDomain(),
            item?.videosAssociated
        )
        FileList.updateItemInImageMetadataList(metadata)
    }

    override suspend fun savePartnerIdAudios(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit> {
        val errorsInFiles = associateToAudiosAndGetErrors(domainFileList, partnerID)
        return if (errorsInFiles.isEmpty()) Result.Success(Unit)
        else Result.Error(Exception("Partner ID could not be associated to: $errorsInFiles"))
    }

    private suspend fun associateToAudiosAndGetErrors(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): MutableList<String> {
        val errorsInFiles = mutableListOf<String>()
        val cameraConnectFileList = domainFileList.toCameraList()

        cameraConnectFileList.forEach { fileItem ->
            val partnerMetadata = AudioMetadata(partnerID = partnerID)
            val cameraAudioInformation = buildAudioInformation(fileItem, partnerMetadata)

            delay(ASSOCIATE_AUDIOS_DELAY)

            val saveResult = getResultWithAttempts(ASSOCIATE_PARTNER_ATTEMPTS) {
                fileListRemoteDataSource.savePartnerIdAudios(cameraAudioInformation)
            }

            if (saveResult is Result.Error) {
                errorsInFiles.add(fileItem.getDateDependingOnNameLength())
            } else updateAudioMetadataInCache(fileItem, cameraAudioInformation)
        }

        return errorsInFiles
    }

    private fun buildAudioInformation(
        fileItem: CameraFile,
        partnerMetadata: AudioMetadata
    ) = AudioInformation(
        fileName = fileItem.name,
        officerId = CameraInfo.officerId,
        path = fileItem.path,
        x1sn = CameraInfo.serialNumber,
        metadata = partnerMetadata,
        nameFolder = fileItem.nameFolder
    )

    private fun updateAudioMetadataInCache(
        fileItem: CameraFile,
        cameraAudioMetadata: AudioInformation
    ) {
        val item = FileList.findAndGetAudioMetadata(fileItem.name)
        val metadata = DomainInformationAudioMetadata(
            cameraAudioMetadata.toDomain(),
            item?.videosAssociated
        )
        FileList.updateItemInAudioMetadataList(metadata)
    }

    companion object {
        private const val ASSOCIATE_VIDEOS_DELAY = 200L
        private const val ASSOCIATE_SNAPSHOTS_DELAY = 150L
        private const val ASSOCIATE_AUDIOS_DELAY = 150L
        private const val ASSOCIATE_ALL_SNAPSHOTS_DELAY = 300L
        private const val ASSOCIATE_PARTNER_ATTEMPTS = 3

        private const val ASSOCIATE_PARTNER_ERROR = "Partner ID could not be associated"
    }
}
