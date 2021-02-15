package com.lawmobile.domain.repository.thumbnailList

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface ThumbnailListRepository : BaseRepository {
    suspend fun getImageBytes(domainCameraFile: DomainCameraFile): Result<DomainInformationImage>
    suspend fun getSnapshotList(): Result<DomainInformationFileResponse>
}
