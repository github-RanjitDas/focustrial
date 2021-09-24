package com.lawmobile.domain.usecase.fileList

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.repository.fileList.FileListRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class FileListUseCaseImpl(private val fileListRepository: FileListRepository) : FileListUseCase {
    override suspend fun savePartnerIdVideos(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit> = fileListRepository.savePartnerIdVideos(domainFileList, partnerID)

    override suspend fun savePartnerIdSnapshot(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit> = fileListRepository.savePartnerIdSnapshot(domainFileList, partnerID)

    override suspend fun savePartnerIdAudios(
        domainFileList: List<DomainCameraFile>,
        partnerID: String
    ): Result<Unit> = fileListRepository.savePartnerIdAudios(domainFileList, partnerID)
}
