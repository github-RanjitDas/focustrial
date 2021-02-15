package com.lawmobile.data.datasource.remote.snapshotDetail

import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result

class SnapshotDetailRemoteDataSourceImpl(private val cameraService: CameraService) :
    SnapshotDetailRemoteDataSource {
    override suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray> {
        return cameraService.getImageBytes(cameraFile)
    }

    override suspend fun savePartnerIdSnapshot(photoInformation: PhotoInformation): Result<Unit> {
        return cameraService.savePhotoMetadata(photoInformation)
    }

    override suspend fun getInformationOfPhoto(cameraFile: CameraFile): Result<PhotoInformation> {
        return cameraService.getPhotoMetadata(cameraFile)
    }

    override suspend fun getVideoList(): Result<FileResponseWithErrors> {
        return cameraService.getListOfVideos()
    }

    override suspend fun getMetadataOfVideo(cameraFile: CameraFile): Result<VideoInformation> {
        return cameraService.getVideoMetadata(cameraFile.name, cameraFile.nameFolder)
    }

    override suspend fun savePartnerIdInAllSnapshots(list: List<PhotoInformation>): Result<Unit> =
        cameraService.saveAllPhotoMetadata(list)

    override suspend fun getSavedPhotosMetadata(): Result<List<PhotoInformation>> =
        cameraService.getMetadataOfPhotos()
}
