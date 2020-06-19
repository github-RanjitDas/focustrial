package com.lawmobile.domain.usecase.linkSnapshotsToVideo

import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.commons.helpers.Result

interface LinkSnapshotsUseCase : BaseUseCase {
    suspend fun getImagesByteList(currentPage: Int): Result<List<DomainInformationImage>>
    fun getImageListSize(): Int
}