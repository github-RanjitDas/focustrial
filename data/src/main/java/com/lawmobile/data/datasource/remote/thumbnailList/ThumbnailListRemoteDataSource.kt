package com.lawmobile.data.datasource.remote.thumbnailList

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ThumbnailListRemoteDataSource {
    suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray>
    suspend fun getSnapshotList(): Result<FileResponseWithErrors>
    suspend fun getVideoList(): Result<FileResponseWithErrors>
}
