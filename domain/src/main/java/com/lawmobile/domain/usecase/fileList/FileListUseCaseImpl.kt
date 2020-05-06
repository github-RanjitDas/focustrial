package com.lawmobile.domain.usecase.fileList

import com.lawmobile.domain.repository.fileList.FileListRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class FileListUseCaseImpl(private val fileListRepository: FileListRepository) : FileListUseCase {
    override suspend fun getSnapshotList(): Result<List<CameraConnectFile>> =
        fileListRepository.getSnapshotList()

    override suspend fun getVideoList(): Result<List<CameraConnectFile>> =
        fileListRepository.getVideoList()
}