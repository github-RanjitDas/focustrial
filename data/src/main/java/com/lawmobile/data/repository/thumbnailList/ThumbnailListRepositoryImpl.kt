package com.lawmobile.data.repository.thumbnailList

import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSource
import com.lawmobile.data.mappers.FileMapper
import com.lawmobile.data.mappers.FileResponseMapper
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ThumbnailListRepositoryImpl(
    private val thumbnailListRemoteDataSource: ThumbnailListRemoteDataSource
) : ThumbnailListRepository {

    override suspend fun getImageBytes(domainCameraFile: DomainCameraFile): Result<DomainInformationImage> {
        val cameraConnectFile = FileMapper.domainToCamera(domainCameraFile)
        return when (val result = thumbnailListRemoteDataSource.getImageBytes(cameraConnectFile)) {
            is Result.Success ->
                Result.Success(
                    DomainInformationImage(
                        domainCameraFile,
                        result.data,
                        false
                    )
                )
            is Result.Error -> result
        }
    }

    override suspend fun getSnapshotList(): Result<DomainInformationFileResponse> =
        when (val response = thumbnailListRemoteDataSource.getSnapshotList()) {
            is Result.Success -> {
                val domainInformationFileResponse = FileResponseMapper.cameraToDomain(response.data)
                if (domainInformationFileResponse.items.size < FileList.imageList.size) {
                    domainInformationFileResponse.items = FileList.imageList as MutableList
                    Result.Success(domainInformationFileResponse)
                } else {
                    FileList.changeImageList(domainInformationFileResponse.items)
                    Result.Success(domainInformationFileResponse)
                }
            }
            is Result.Error -> response
        }
}
