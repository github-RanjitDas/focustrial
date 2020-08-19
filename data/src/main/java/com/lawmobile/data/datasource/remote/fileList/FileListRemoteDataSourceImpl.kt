package com.lawmobile.data.datasource.remote.fileList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class FileListRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    FileListRemoteDataSource {

    override suspend fun savePartnerIdVideos(cameraConnectVideoMetadata: CameraConnectVideoMetadata): Result<Unit> =
        cameraConnectService.saveVideoMetadata(cameraConnectVideoMetadata)

    override suspend fun savePartnerIdSnapshot(cameraConnectPhotoMetadata: CameraConnectPhotoMetadata): Result<Unit> =
        cameraConnectService.savePhotoMetadata(cameraConnectPhotoMetadata)
}