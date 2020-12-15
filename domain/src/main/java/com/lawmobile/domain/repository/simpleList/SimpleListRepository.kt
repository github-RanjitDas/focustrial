package com.lawmobile.domain.repository.simpleList

import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface SimpleListRepository : BaseRepository {
    suspend fun getSnapshotList(): Result<DomainInformationFileResponse>
    suspend fun getVideoList(): Result<DomainInformationFileResponse>
}