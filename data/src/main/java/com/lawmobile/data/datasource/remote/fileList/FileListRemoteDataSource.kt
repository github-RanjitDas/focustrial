package com.lawmobile.data.datasource.remote.fileList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.commons.helpers.Result

interface FileListRemoteDataSource {
    suspend fun getSnapshotList(): Result<CameraConnectFileResponseWithErrors>
    suspend fun getVideoList(): Result<CameraConnectFileResponseWithErrors>
    suspend fun savePartnerIdVideos(
        cameraConnectVideoMetadata: CameraConnectVideoMetadata
    ): Result<Unit>

    suspend fun savePartnerIdSnapshot(
        cameraConnectPhotoMetadata: CameraConnectPhotoMetadata
    ): Result<Unit>
}