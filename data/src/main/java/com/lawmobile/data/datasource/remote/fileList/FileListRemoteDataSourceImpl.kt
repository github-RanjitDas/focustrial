package com.lawmobile.data.datasource.remote.fileList

import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result

class FileListRemoteDataSourceImpl(private val cameraConnectService: CameraService) :
    FileListRemoteDataSource {

    override suspend fun savePartnerIdVideos(
        videoInformation: VideoInformation
    ): Result<Unit> = cameraConnectService.saveVideoMetadata(videoInformation)

    override suspend fun savePartnerIdInAllSnapshots(
        list: List<PhotoInformation>
    ): Result<Unit> = cameraConnectService.saveAllPhotoMetadata(list)

    override suspend fun getSavedPhotosMetadata(): Result<List<PhotoInformation>> =
        cameraConnectService.getMetadataOfPhotos()

    override suspend fun savePartnerIdSnapshot(photoInformation: PhotoInformation): Result<Unit> =
        cameraConnectService.savePhotoMetadata(photoInformation)
}
