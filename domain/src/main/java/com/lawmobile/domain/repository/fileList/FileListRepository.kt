package com.lawmobile.domain.repository.fileList

import com.lawmobile.domain.entities.DomainCameraFile
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface FileListRepository {
    suspend fun savePartnerIdVideos(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit>

    suspend fun savePartnerIdSnapshot(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit>

    suspend fun savePartnerIdAudios(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit>
}
