package com.lawmobile.data.datasource.remote.fileList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class FileListRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    FileListRemoteDataSource {

    override suspend fun savePartnerIdVideos(cameraConnectVideoMetadata: CameraConnectVideoMetadata): Result<Unit> =
        cameraConnectService.saveVideoMetadata(cameraConnectVideoMetadata)

    override suspend fun savePartnerIdInAllSnapshots(list: List<CameraConnectPhotoMetadata>): Result<Unit> =
        cameraConnectService.saveAllPhotoMetadata(list)

    override suspend fun getSavedPhotosMetadata(): Result<List<CameraConnectPhotoMetadata>>  =
        cameraConnectService.getMetadataOfPhotos()

    override suspend fun savePartnerIdSnapshot(cameraConnectPhotoMetadata: CameraConnectPhotoMetadata): Result<Unit> =
        cameraConnectService.savePhotoMetadata(cameraConnectPhotoMetadata)
}