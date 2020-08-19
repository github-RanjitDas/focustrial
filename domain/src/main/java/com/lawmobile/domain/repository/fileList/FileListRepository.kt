package com.lawmobile.domain.repository.fileList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface FileListRepository {
    suspend fun savePartnerIdVideos(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit>

    suspend fun savePartnerIdSnapshot(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit>
}