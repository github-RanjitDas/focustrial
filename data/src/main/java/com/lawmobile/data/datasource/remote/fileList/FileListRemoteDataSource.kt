package com.lawmobile.data.datasource.remote.fileList

import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface FileListRemoteDataSource {
    suspend fun savePartnerIdVideos(
        videoInformation: VideoInformation
    ): Result<Unit>

    suspend fun savePartnerIdInAllSnapshots(
        list: List<PhotoInformation>
    ): Result<Unit>

    suspend fun getSavedPhotosMetadata(): Result<List<PhotoInformation>>

    suspend fun savePartnerIdSnapshot(photoInformation: PhotoInformation): Result<Unit>
}
