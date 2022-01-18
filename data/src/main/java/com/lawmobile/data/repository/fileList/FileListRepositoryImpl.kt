package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.extensions.getDateDependingOnNameLength
import com.lawmobile.data.mappers.FileMapper
import com.lawmobile.data.mappers.PhotoMetadataMapper
import com.lawmobile.data.mappers.VideoMetadataMapper
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.entities.RemoteVideoMetadata
import com.lawmobile.domain.entities.VideoListMetadata
import com.lawmobile.domain.repository.fileList.FileListRepository
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
        val cameraConnectFileList = FileMapper.domainToCameraList(domainFileList)
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
        val remoteVideoMetadata = VideoListMetadata.getVideoMetadata(it.name)?.videoMetadata

        if (remoteVideoMetadata?.metadata != null) {
            remoteVideoMetadata.metadata!!.partnerID = partnerID
            remoteVideoMetadata.nameFolder = it.nameFolder
            videoInformation = VideoMetadataMapper.domainToCamera(remoteVideoMetadata)
        } else {
            val partnerMetadata = VideoMetadata(partnerID = partnerID)
            videoInformation = buildVideoInformation(it, remoteVideoMetadata, partnerMetadata)
        }
        return videoInformation
    }

    private fun buildVideoInformation(
        it: CameraFile,
        remoteVideoMetadata: DomainVideoMetadata?,
        partnerMetadata: VideoMetadata
    ) = VideoInformation(
        fileName = it.name,
        officerId = CameraInfo.officerId,
        x1sn = CameraInfo.serialNumber,
        path = remoteVideoMetadata?.path ?: it.path,
        metadata = partnerMetadata,
        nameFolder = it.nameFolder
    )

    private fun updateVideoMetadataInCache(videoInformation: VideoInformation) {
        val domainMetadata = VideoMetadataMapper.cameraToDomain(videoInformation)
        val remoteMetadata = RemoteVideoMetadata(domainMetadata, true)
        VideoListMetadata.saveOrUpdateVideoMetadata(remoteMetadata)
    }

    override suspend fun savePartnerIdSnapshot(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit> {
        val photoMetadataResult = getResultWithAttempts(ASSOCIATE_PARTNER_ATTEMPTS) {
            fileListRemoteDataSource.getSavedPhotosMetadata()
        }
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
        val cameraConnectFileList = FileMapper.domainToCameraList(domainFileList)

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
        val item = FileList.getMetadataOfImageInList(fileItem.name)
        val metadata = DomainInformationImageMetadata(
            PhotoMetadataMapper.cameraToDomain(cameraPhotoMetadata),
            item?.videosAssociated
        )
        FileList.updateItemInImageMetadataList(metadata)
    }

    companion object {
        private const val ASSOCIATE_VIDEOS_DELAY = 200L
        private const val ASSOCIATE_SNAPSHOTS_DELAY = 150L
        private const val ASSOCIATE_ALL_SNAPSHOTS_DELAY = 300L
        private const val ASSOCIATE_PARTNER_ATTEMPTS = 3

        private const val ASSOCIATE_PARTNER_ERROR = "Partner ID could not be associated"
    }
}
