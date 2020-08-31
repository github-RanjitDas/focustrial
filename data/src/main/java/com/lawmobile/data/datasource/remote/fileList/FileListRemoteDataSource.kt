package com.lawmobile.data.datasource.remote.fileList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.commons.helpers.Result

interface FileListRemoteDataSource {
    suspend fun savePartnerIdVideos(
        cameraConnectVideoMetadata: CameraConnectVideoMetadata
    ): Result<Unit>

    suspend fun savePartnerIdInAllSnapshots(
        list: List<CameraConnectPhotoMetadata>
    ): Result<Unit>

    suspend fun getSavedPhotosMetadata(): Result<List<CameraConnectPhotoMetadata>>

    suspend fun savePartnerIdSnapshot(cameraConnectPhotoMetadata: CameraConnectPhotoMetadata): Result<Unit>
}