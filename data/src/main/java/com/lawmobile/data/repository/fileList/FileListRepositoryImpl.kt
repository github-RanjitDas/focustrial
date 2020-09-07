package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.data.entities.RemoteVideoMetadata
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.repository.fileList.FileListRepository
import com.safefleet.mobile.avml.cameras.entities.*
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfSuccess
import kotlinx.coroutines.delay

class FileListRepositoryImpl(private val fileListRemoteDataSource: FileListRemoteDataSource) :
    FileListRepository {

    override suspend fun savePartnerIdVideos(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit> {
        val errorsInFiles = ArrayList<String>()
        cameraConnectFileList.forEach {
            val cameraConnectVideoMetadata: CameraConnectVideoMetadata?
            val remoteVideoMetadata = VideoListMetadata.getVideoMetadata(it.name)
            if (remoteVideoMetadata != null && remoteVideoMetadata.videoMetadata.metadata != null) {
                remoteVideoMetadata.videoMetadata.metadata!!.partnerID = partnerID
                remoteVideoMetadata.videoMetadata.nameFolder = it.nameFolder
                cameraConnectVideoMetadata = remoteVideoMetadata.videoMetadata
            } else {
                val partnerMetadata = VideoMetadata(partnerID = partnerID)
                cameraConnectVideoMetadata = CameraConnectVideoMetadata(
                    fileName = it.name,
                    officerId = CameraInfo.officerId,
                    x1sn = CameraInfo.serialNumber,
                    path = it.path,
                    metadata = partnerMetadata,
                    nameFolder = it.nameFolder
                )
            }

            delay(200)
            val result = fileListRemoteDataSource.savePartnerIdVideos(cameraConnectVideoMetadata)
            if (result is Result.Error) {
                errorsInFiles.add(it.name)
            } else {
                VideoListMetadata.saveOrUpdateVideoMetadata(
                    RemoteVideoMetadata(
                        cameraConnectVideoMetadata,
                        true
                    )
                )
            }
        }
        return if (errorsInFiles.isEmpty()) Result.Success(Unit)
        else Result.Error(Exception("Partner ID could not be associated to: $errorsInFiles"))
    }

    override suspend fun savePartnerIdSnapshot(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit> {
        val itemsFinal = mutableListOf<CameraConnectPhotoMetadata>()
        val listPhotosSaved = ArrayList<CameraConnectPhotoMetadata>()
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
            val cameraPhotoMetadata = CameraConnectPhotoMetadata(
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
                FileList.updateItemInListImageMetadata(DomainInformationImageMetadata(cameraPhotoMetadata))
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