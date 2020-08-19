package com.lawmobile.domain.usecase.thumbnailList

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.commons.helpers.Result

interface ThumbnailListUseCase : BaseUseCase {
    suspend fun getImagesByteList(currentPage: Int): Result<List<DomainInformationImage>>
    fun getImageListSize(): Int
}