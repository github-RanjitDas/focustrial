package com.lawmobile.domain.usecase.thumbnailList

import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class ThumbnailListUseCaseImpl(private val thumbnailListRepository: ThumbnailListRepository) :
    ThumbnailListUseCase {
    override suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<DomainInformationImage> =
        thumbnailListRepository.getImageBytes(cameraConnectFile)

    override suspend fun getSnapshotList(): Result<DomainInformationFileResponse> =
        thumbnailListRepository.getSnapshotList()
}