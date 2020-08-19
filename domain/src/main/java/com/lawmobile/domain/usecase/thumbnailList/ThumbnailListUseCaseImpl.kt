package com.lawmobile.domain.usecase.thumbnailList

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
import com.safefleet.mobile.commons.helpers.Result

class ThumbnailListUseCaseImpl(private val thumbnailListRepository: ThumbnailListRepository) :
    ThumbnailListUseCase {
    override suspend fun getImagesByteList(currentPage: Int): Result<List<DomainInformationImage>> =
        thumbnailListRepository.getImageByteList(currentPage)

    override fun getImageListSize(): Int =
        thumbnailListRepository.getImageListSize()
}