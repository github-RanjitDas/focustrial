package com.lawmobile.data.datasource.remote.snapshotDetail

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.commons.helpers.Result

interface SnapshotDetailRemoteDataSource {
    suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<ByteArray>
    suspend fun savePartnerIdSnapshot(cameraConnectPhotoMetadata: CameraConnectPhotoMetadata): Result<Unit>
    suspend fun getInformationOfPhoto(cameraFile: CameraConnectFile): Result<CameraConnectPhotoMetadata>
    suspend fun getVideoList(): Result<CameraConnectFileResponseWithErrors>
    suspend fun getMetadataOfVideo(cameraConnectFile: CameraConnectFile): Result<CameraConnectVideoMetadata>
}