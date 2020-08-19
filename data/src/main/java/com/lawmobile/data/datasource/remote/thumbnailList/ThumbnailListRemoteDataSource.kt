package com.lawmobile.data.datasource.remote.thumbnailList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.commons.helpers.Result

interface ThumbnailListRemoteDataSource {
    suspend fun getImageBytes(snapshot: CameraConnectFile): Result<ByteArray>
    suspend fun getSnapshotList(): Result<CameraConnectFileResponseWithErrors>
}