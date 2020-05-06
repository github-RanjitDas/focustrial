package com.lawmobile.data.datasource.remote.fileList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface FileListRemoteDataSource {
    suspend fun getSnapshotList(): Result<List<CameraConnectFile>>
    suspend fun getVideoList(): Result<List<CameraConnectFile>>
}