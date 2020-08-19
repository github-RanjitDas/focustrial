package com.lawmobile.domain.usecase.fileList

import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface FileListUseCase : BaseUseCase {
    suspend fun savePartnerIdVideos(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit>

    suspend fun savePartnerIdSnapshot(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit>
}