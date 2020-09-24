package com.lawmobile.data.repository.thumbnailList

import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class ThumbnailListRepositoryImpl(
    private val thumbnailListRemoteDataSource: ThumbnailListRemoteDataSource
) : ThumbnailListRepository {

    override suspend fun getImageBytes(cameraConnectFile: CameraConnectFile): Result<DomainInformationImage> =
        when (val result = thumbnailListRemoteDataSource.getImageBytes(cameraConnectFile)) {
            is Result.Success ->
                Result.Success(
                    DomainInformationImage(
                        cameraConnectFile,
                        result.data,
                        false
                    )
                )
            is Result.Error -> result
        }

    override suspend fun getSnapshotList(): Result<DomainInformationFileResponse> {
        return when (val response = thumbnailListRemoteDataSource.getSnapshotList()) {
            is Result.Success -> {
                val domainInformationFileResponse = DomainInformationFileResponse()
                val items = response.data.items.sortedByDescending { it.date }.map {
                    DomainInformationFile(it, null)
                }
                domainInformationFileResponse.errors.addAll(response.data.errors)
                if (items.size < FileList.listOfImages.size) {
                    domainInformationFileResponse.listItems.addAll(FileList.listOfImages)
                    return Result.Success(domainInformationFileResponse)
                }

                domainInformationFileResponse.listItems.addAll(items)
                FileList.changeListOfImages(items)
                return Result.Success(domainInformationFileResponse)
            }
            is Result.Error -> response
        }
    }
}