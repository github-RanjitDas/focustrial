package com.lawmobile.domain.repository.thumbnailList

import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface ThumbnailListRepository : BaseRepository {
    suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<DomainInformationImage>
    suspend fun getSnapshotList(): Result<DomainInformationFileResponse>
}