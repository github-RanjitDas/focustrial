package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.mappers.impl.FileMapper.toCameraList
import com.lawmobile.data.mappers.impl.PhotoMetadataMapper.toDomain
import com.lawmobile.data.mappers.impl.VideoMetadataMapper.toCamera
import com.lawmobile.data.mappers.impl.VideoMetadataMapper.toDomain
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.entities.RemoteVideoMetadata
import com.lawmobile.domain.entities.VideoListMetadata
import com.lawmobile.domain.repository.fileList.FileListRepository
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.VideoMetadata
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.delay

class FileListRepositoryImpl(
    private val fileListRemoteDataSource: FileListRemoteDataSource
) : FileListRepository {

    override suspend fun savePartnerIdVideos(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit> {
        val cameraConnectFileList = domainFileList.toCameraList()
        val errorsInFiles = ArrayList<String>()

        cameraConnectFileList.forEach {
            val videoInformation: VideoInformation?
            val remoteVideoMetadata = VideoListMetadata.getVideoMetadata(it.name)?.videoMetadata

            if (remoteVideoMetadata?.metadata != null) {
                remoteVideoMetadata.metadata!!.partnerID = partnerID
                remoteVideoMetadata.nameFolder = it.nameFolder
                videoInformation = remoteVideoMetadata.toCamera()
            } else {
                val partnerMetadata = VideoMetadata(partnerID = partnerID)
                videoInformation = VideoInformation(
                    fileName = it.name,
                    officerId = CameraInfo.officerId,
                    x1sn = CameraInfo.serialNumber,
                    path = remoteVideoMetadata?.path ?: it.path,
                    metadata = partnerMetadata,
                    nameFolder = it.nameFolder
                )
            }

            delay(200)
            val result = fileListRemoteDataSource.savePartnerIdVideos(videoInformation)
            if (result is Result.Error) {
                errorsInFiles.add(it.name)
            } else {
                VideoListMetadata.saveOrUpdateVideoMetadata(
                    RemoteVideoMetadata(videoInformation.toDomain(), true)
                )
            }
        }
        return if (errorsInFiles.isEmpty()) Result.Success(Unit)
        else Result.Error(Exception("Partner ID could not be associated to: $errorsInFiles"))
    }

    override suspend fun savePartnerIdSnapshot(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit> {
        val cameraConnectFileList = domainFileList.toCameraList()
        val itemsFinal = mutableListOf<PhotoInformation>()
        val listPhotosSaved = ArrayList<PhotoInformation>()
        val resultGetMetadataOfPhotos = fileListRemoteDataSource.getSavedPhotosMetadata()

        if (resultGetMetadataOfPhotos is Result.Error) return resultGetMetadataOfPhotos

        listPhotosSaved.addAll((resultGetMetadataOfPhotos as Result.Success).data)
        itemsFinal.addAll(listPhotosSaved)
        val errorsInFiles = ArrayList<String>()

        cameraConnectFileList.forEach { fileItem ->
            itemsFinal.removeAll { it.fileName == fileItem.name }
            val partnerMetadata = PhotoMetadata(partnerID = partnerID)
            val cameraPhotoMetadata = PhotoInformation(
                fileName = fileItem.name,
                officerId = CameraInfo.officerId,
                path = fileItem.path,
                x1sn = CameraInfo.serialNumber,
                metadata = partnerMetadata,
                nameFolder = fileItem.nameFolder
            )

            delay(150)

            val resultPartnerOnly =
                fileListRemoteDataSource.savePartnerIdSnapshot(cameraPhotoMetadata)
            itemsFinal.removeAll { it.fileName == fileItem.name }
            itemsFinal.add(cameraPhotoMetadata)

            if (resultPartnerOnly is Result.Error) {
                errorsInFiles.add(cameraPhotoMetadata.fileName)
            } else {
                val item = FileList.getMetadataOfImageInList(fileItem.name)
                val newItemPhoto = DomainInformationImageMetadata(
                    cameraPhotoMetadata.toDomain(),
                    item?.videosAssociated
                )
                FileList.updateItemInImageMetadataList(newItemPhoto)
            }
        }

        delay(300)

        val resultJSONOnly = fileListRemoteDataSource.savePartnerIdInAllSnapshots(itemsFinal)
        resultJSONOnly.doIfSuccess {
            return if (errorsInFiles.isEmpty()) Result.Success(Unit)
            else Result.Error(java.lang.Exception("Partner ID could not be associated to: $errorsInFiles"))
        }

        return Result.Error(java.lang.Exception("Partner ID could not be associated"))
    }
}
