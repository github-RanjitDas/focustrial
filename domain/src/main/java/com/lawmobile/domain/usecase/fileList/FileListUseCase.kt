package com.lawmobile.domain.usecase.fileList

import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface FileListUseCase : BaseUseCase {
    suspend fun getSnapshotList(): Result<List<CameraConnectFile>>
    suspend fun getVideoList(): Result<List<CameraConnectFile>>
}