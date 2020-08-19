package com.lawmobile.domain.repository.thumbnailList

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.commons.helpers.Result

interface ThumbnailListRepository : BaseRepository {
    suspend fun getImageByteList(currentPage: Int): Result<List<DomainInformationImage>>
    fun getImageListSize(): Int
}