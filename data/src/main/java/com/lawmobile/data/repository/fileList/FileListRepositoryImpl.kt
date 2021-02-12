package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.entities.RemoteVideoMetadata
import com.lawmobile.domain.entities.VideoListMetadata
import com.lawmobile.data.mappers.FileMapper
import com.lawmobile.data.mappers.PhotoMetadataMapper
import com.lawmobile.data.mappers.VideoMetadataMapper
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.repository.fileList.FileListRepository
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.VideoMetadata
import kotlinx.coroutines.delay

class FileListRepositoryImpl(private val fileListRemoteDataSource: FileListRemoteDataSource) :
    FileListRepository {

    override suspend fun savePartnerIdVideos(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit> {
        val cameraConnectFileList = FileMapper.domainToCameraList(domainFileList)
        val errorsInFiles = ArrayList<String>()

        cameraConnectFileList.forEach {
            val videoInformation: VideoInformation?
            val remoteVideoMetadata = VideoListMetadata.getVideoMetadata(it.name)?.videoMetadata

            if (remoteVideoMetadata?.metadata != null) {
                remoteVideoMetadata.metadata!!.partnerID = partnerID
                remoteVideoMetadata.nameFolder = it.nameFolder
                videoInformation = VideoMetadataMapper.domainToCamera(remoteVideoMetadata)
            } else {
                val partnerMetadata = VideoMetadata(partnerID = partnerID)
                videoInformation = VideoInformation(
                    fileName = it.name,
                    officerId = CameraInfo.officerId,
                    x1sn = CameraInfo.serialNumber,
                    path = it.path,
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
                    RemoteVideoMetadata(
                        VideoMetadataMapper.cameraToDomain(videoInformation),
                        true
                    )
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
        val cameraConnectFileList = FileMapper.domainToCameraList(domainFileList)
        val itemsFinal = mutableListOf<PhotoInformation>()
        val listPhotosSaved = ArrayList<PhotoInformation>()
        val resultGetMetadataOfPhotos = fileListRemoteDataSource.getSavedPhotosMetadata()

        if (resultGetMetadataOfPhotos is Result.Error) {
            return resultGetMetadataOfPhotos
        }

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
                    PhotoMetadataMapper.cameraToDomain(cameraPhotoMetadata),
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