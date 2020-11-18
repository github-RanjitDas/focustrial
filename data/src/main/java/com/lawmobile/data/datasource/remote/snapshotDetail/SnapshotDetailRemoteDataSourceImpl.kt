package com.lawmobile.data.datasource.remote.snapshotDetail

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class SnapshotDetailRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    SnapshotDetailRemoteDataSource {
    override suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray> {
        return cameraConnectService.getImageBytes(cameraConnectFile)
    }

    override suspend fun savePartnerIdSnapshot(cameraConnectPhotoMetadata: CameraConnectPhotoMetadata): Result<Unit> {
        return cameraConnectService.savePhotoMetadata(cameraConnectPhotoMetadata)
    }

    override suspend fun getInformationOfPhoto(cameraFile: CameraConnectFile): Result<CameraConnectPhotoMetadata> {
        return cameraConnectService.getPhotoMetadata(cameraFile)
    }

    override suspend fun getVideoList(): Result<CameraConnectFileResponseWithErrors> {
        return  cameraConnectService.getListOfVideos()
    }

    override suspend fun getMetadataOfVideo(cameraConnectFile: CameraConnectFile): Result<CameraConnectVideoMetadata> {
        return cameraConnectService.getVideoMetadata(cameraConnectFile.name, cameraConnectFile.nameFolder)
    }

    override suspend fun savePartnerIdInAllSnapshots(list: List<CameraConnectPhotoMetadata>): Result<Unit> =
        cameraConnectService.saveAllPhotoMetadata(list)

    override suspend fun getSavedPhotosMetadata(): Result<List<CameraConnectPhotoMetadata>>  =
        cameraConnectService.getMetadataOfPhotos()
}