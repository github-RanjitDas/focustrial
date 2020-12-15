package com.lawmobile.domain.usecase.simpleList

import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface SimpleListUseCase : BaseUseCase {
    suspend fun getSnapshotList(): Result<DomainInformationFileResponse>
    suspend fun getVideoList(): Result<DomainInformationFileResponse>
}