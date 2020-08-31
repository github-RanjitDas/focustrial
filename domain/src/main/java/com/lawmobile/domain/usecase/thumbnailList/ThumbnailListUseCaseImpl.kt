package com.lawmobile.domain.usecase.thumbnailList

import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class ThumbnailListUseCaseImpl(private val thumbnailListRepository: ThumbnailListRepository) :
    ThumbnailListUseCase {
    override suspend fun getImagesByteList(cameraConnectFile: CameraConnectFile): Result<List<DomainInformationImage>> =
        thumbnailListRepository.getImageByteList(cameraConnectFile)

    override suspend fun getImageList(): Result<List<DomainInformationFile>> =
        thumbnailListRepository.getImageList()
}