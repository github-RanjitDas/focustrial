package com.lawmobile.data.datasource.remote.file

import com.lawmobile.body_cameras.entities.CameraFile
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface FileRemoteDataSource {
    suspend fun getFileBytes(file: CameraFile): Result<ByteArray>
    suspend fun saveFailSafeVideo(fileBytes: ByteArray): Result<Unit>
}
