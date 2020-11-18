package com.lawmobile.domain.repository.linkSnapshotsToVideo

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.commons.helpers.Result

interface LinkSnapshotsRepository : BaseRepository {
    suspend fun getImageByteList(currentPage: Int): Result<List<DomainInformationImage>>
    fun getImageListSize(): Int
}