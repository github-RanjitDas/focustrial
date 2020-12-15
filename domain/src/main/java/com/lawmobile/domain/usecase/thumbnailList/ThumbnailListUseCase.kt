package com.lawmobile.domain.usecase.thumbnailList

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ThumbnailListUseCase : BaseUseCase {
    suspend fun getImageBytes(domainCameraFile: DomainCameraFile): Result<DomainInformationImage>
    suspend fun getSnapshotList(): Result<DomainInformationFileResponse>
}