package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.data.entities.RemoteVideoMetadata
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.domain.CameraInfo
import com.lawmobile.domain.entity.DomainInformationFile
import com.lawmobile.domain.repository.fileList.FileListRepository
import com.safefleet.mobile.avml.cameras.entities.*
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.delay

class FileListRepositoryImpl(private val fileListRemoteDataSource: FileListRemoteDataSource) :
    FileListRepository {
    override suspend fun getSnapshotList(): Result<List<DomainInformationFile>> {
        return when (val response = fileListRemoteDataSource.getSnapshotList()) {
            is Result.Success -> {
                val items = response.data.map {
                    DomainInformationFile(it, null)
                }
                if (items.size < FileList.listOfImages.size){
                    return Result.Success(FileList.listOfImages)
                }

                FileList.changeListOfImages(items)
                return Result.Success(items)
            }
            is Result.Error -> response
        }
    }

    override suspend fun getVideoList(): Result<List<DomainInformationFile>> {
        return when (val response = fileListRemoteDataSource.getVideoList()) {
            is Result.Success -> {
                val items = response.data.map {
                    val remoteMetadata = VideoListMetadata.getVideoMetadata(it.name)
                    DomainInformationFile(it, remoteMetadata?.videoMetadata)
                }
                if (items.size < FileList.listOfVideos.size){
                    return Result.Success(FileList.listOfVideos.map {
                        val remoteMetadata = VideoListMetadata.getVideoMetadata(it.cameraConnectFile.name)
                        DomainInformationFile(it.cameraConnectFile, remoteMetadata?.videoMetadata)
                    })
                }

                FileList.changeListOfVideos(items)
                return Result.Success(items)
            }
            is Result.Error -> response
        }
    }


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

            delay(100)
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
        val errorsInFiles = ArrayList<String>()
        cameraConnectFileList.forEach {

            val partnerMetadata = PhotoMetadata(partnerID = partnerID)
            val cameraConnectSnapshotMetadata = CameraConnectPhotoMetadata(
                fileName = it.name,
                officerId = CameraInfo.officerId,
                path = it.path,
                x1sn = CameraInfo.serialNumber,
                metadata = partnerMetadata,
                nameFolder = it.nameFolder
            )
            delay(100)
            val result =
                fileListRemoteDataSource.savePartnerIdSnapshot(cameraConnectSnapshotMetadata)
            if (result is Result.Error) {
                errorsInFiles.add(it.name)
            }
        }
        return if (errorsInFiles.isEmpty()) Result.Success(Unit)
        else Result.Error(Exception("Partner ID could not be associated to: $errorsInFiles"))
    }
}