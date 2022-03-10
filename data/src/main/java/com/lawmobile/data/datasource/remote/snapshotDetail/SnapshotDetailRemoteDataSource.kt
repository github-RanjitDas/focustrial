package com.lawmobile.data.datasource.remote.snapshotDetail

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface SnapshotDetailRemoteDataSource {
    suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray>
    suspend fun savePartnerIdSnapshot(photoInformation: PhotoInformation): Result<Unit>
    suspend fun getInformationOfPhoto(cameraFile: CameraFile): Result<PhotoInformation>
    suspend fun savePartnerIdInAllSnapshots(
        list: List<PhotoInformation>
    ): Result<Unit>

    suspend fun getSavedPhotosMetadata(): Result<List<PhotoInformation>>
}
