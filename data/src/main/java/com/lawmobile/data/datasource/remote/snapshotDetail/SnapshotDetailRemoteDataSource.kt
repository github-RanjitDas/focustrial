package com.lawmobile.data.datasource.remote.snapshotDetail

import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface SnapshotDetailRemoteDataSource {
    suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray>
    suspend fun savePartnerIdSnapshot(photoInformation: PhotoInformation): Result<Unit>
    suspend fun getInformationOfPhoto(cameraFile: CameraFile): Result<PhotoInformation>
    suspend fun getVideoList(): Result<FileResponseWithErrors>
    suspend fun getMetadataOfVideo(cameraFile: CameraFile): Result<VideoInformation>
    suspend fun savePartnerIdInAllSnapshots(
        list: List<PhotoInformation>
    ): Result<Unit>

    suspend fun getSavedPhotosMetadata(): Result<List<PhotoInformation>>
}
