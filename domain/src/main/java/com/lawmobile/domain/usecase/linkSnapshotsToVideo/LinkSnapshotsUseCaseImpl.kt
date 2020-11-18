package com.lawmobile.domain.usecase.linkSnapshotsToVideo

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.linkSnapshotsToVideo.LinkSnapshotsRepository
import com.safefleet.mobile.commons.helpers.Result

class LinkSnapshotsUseCaseImpl(private val linkSnapshotsRepository: LinkSnapshotsRepository) :
    LinkSnapshotsUseCase {
    override suspend fun getImagesByteList(currentPage: Int): Result<List<DomainInformationImage>> =
        linkSnapshotsRepository.getImageByteList(currentPage)

    override fun getImageListSize(): Int =
        linkSnapshotsRepository.getImageListSize()
}