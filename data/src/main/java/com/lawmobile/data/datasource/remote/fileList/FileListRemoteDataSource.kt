package com.lawmobile.data.datasource.remote.fileList

import com.lawmobile.body_cameras.entities.AudioInformation
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.body_cameras.entities.VideoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface FileListRemoteDataSource {
    suspend fun savePartnerIdVideos(videoInformation: VideoInformation): Result<Unit>
    suspend fun savePartnerIdSnapshot(photoInformation: PhotoInformation): Result<Unit>
    suspend fun savePartnerIdAudios(audioInformation: AudioInformation): Result<Unit>
    suspend fun savePartnerIdInAllSnapshots(list: List<PhotoInformation>): Result<Unit>
    suspend fun getSavedPhotosMetadata(): Result<List<PhotoInformation>>
}
