package com.lawmobile.domain.repository.fileList

import com.lawmobile.domain.entities.DomainInformationFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface FileListRepository {
    suspend fun getSnapshotList(): Result<List<DomainInformationFile>>
    suspend fun getVideoList(): Result<List<DomainInformationFile>>
    suspend fun savePartnerIdVideos(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit>

    suspend fun savePartnerIdSnapshot(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit>
}