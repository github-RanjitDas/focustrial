package com.lawmobile.domain.usecase.fileList

import com.lawmobile.domain.entity.DomainInformationFile
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface FileListUseCase : BaseUseCase {
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