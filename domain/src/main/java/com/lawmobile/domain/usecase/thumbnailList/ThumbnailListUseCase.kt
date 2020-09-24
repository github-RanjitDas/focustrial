package com.lawmobile.domain.usecase.thumbnailList

import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

interface ThumbnailListUseCase : BaseUseCase {
    suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<DomainInformationImage>
    suspend fun getSnapshotList(): Result<DomainInformationFileResponse>
}