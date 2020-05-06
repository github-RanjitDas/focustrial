package com.lawmobile.domain.repository.fileList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface FileListRepository {
    suspend fun getSnapshotList(): Result<List<CameraConnectFile>>
    suspend fun getVideoList(): Result<List<CameraConnectFile>>
}