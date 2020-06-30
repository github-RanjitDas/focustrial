package com.lawmobile.domain.repository.fileList

import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface FileListRepository {
    suspend fun getSnapshotList(): Result<DomainInformationFileResponse>
    suspend fun getVideoList(): Result<DomainInformationFileResponse>
    suspend fun savePartnerIdVideos(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit>

    suspend fun savePartnerIdSnapshot(
        cameraConnectFileList: List<CameraConnectFile>,
        partnerID: String
    ): Result<Unit>
}