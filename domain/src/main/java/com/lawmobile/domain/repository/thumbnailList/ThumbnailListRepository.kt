package com.lawmobile.domain.repository.thumbnailList

import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface ThumbnailListRepository : BaseRepository {
    suspend fun getImageByteList(cameraConnectFile: CameraConnectFile): Result<List<DomainInformationImage>>
    suspend fun getImageList(): Result<List<DomainInformationFile>>
}