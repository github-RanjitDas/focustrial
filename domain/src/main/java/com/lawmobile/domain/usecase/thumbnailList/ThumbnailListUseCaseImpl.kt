package com.lawmobile.domain.usecase.thumbnailList

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository

class ThumbnailListUseCaseImpl(private val thumbnailListRepository: ThumbnailListRepository) :
    ThumbnailListUseCase {
    override suspend fun getImageBytes(domainCameraFile: DomainCameraFile) =
        thumbnailListRepository.getImageBytes(domainCameraFile)

    override suspend fun getSnapshotList() = thumbnailListRepository.getSnapshotList()
}
