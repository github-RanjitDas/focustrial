package com.lawmobile.data.datasource.remote.thumbnailList

import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ThumbnailListRemoteDataSource {
    suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray>
    suspend fun getSnapshotList(): Result<FileResponseWithErrors>
}
