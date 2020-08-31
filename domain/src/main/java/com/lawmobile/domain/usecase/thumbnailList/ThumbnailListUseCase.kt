package com.lawmobile.domain.usecase.thumbnailList

import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface ThumbnailListUseCase : BaseUseCase {
    suspend fun getImagesByteList(cameraConnectFile: CameraConnectFile): Result<List<DomainInformationImage>>
    suspend fun getImageList(): Result<List<DomainInformationFile>>
}