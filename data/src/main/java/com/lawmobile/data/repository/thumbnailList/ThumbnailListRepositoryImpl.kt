package com.lawmobile.data.repository.thumbnailList

import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSource
import com.lawmobile.data.mappers.impl.FileMapper.toCamera
import com.lawmobile.data.mappers.impl.FileResponseMapper.toDomain
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.entities.VideoListMetadata
import com.lawmobile.domain.enums.RequestError
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ThumbnailListRepositoryImpl(
    private val thumbnailListRemoteDataSource: ThumbnailListRemoteDataSource
) : ThumbnailListRepository {

    override suspend fun getImageBytes(domainCameraFile: DomainCameraFile): Result<DomainInformationImage> {
        val cameraConnectFile = domainCameraFile.toCamera()
        return when (val result = thumbnailListRemoteDataSource.getImageBytes(cameraConnectFile)) {
            is Result.Success -> {
                Result.Success(
                    DomainInformationImage(
                        domainCameraFile,
                        result.data,
                        false
                    )
                )
            }
            is Result.Error -> {
                result
            }
        }
    }

    override suspend fun getSnapshotList(): Result<DomainInformationFileResponse> =
        when (val response = thumbnailListRemoteDataSource.getSnapshotList()) {
            is Result.Success -> {
                val domainInformationFileResponse = response.data.toDomain()
                if (domainInformationFileResponse.items.size < FileList.imageList.size) {
                    domainInformationFileResponse.items = FileList.imageList as MutableList
                    Result.Success(domainInformationFileResponse)
                } else {
                    FileList.imageList = domainInformationFileResponse.items
                    Result.Success(domainInformationFileResponse)
                }
            }
            is Result.Error -> response
        }

    override suspend fun getVideoList(): Result<DomainInformationFileResponse> {
        return when (val response = thumbnailListRemoteDataSource.getVideoList()) {
            is Result.Success -> {
                val domainInformationFileResponse = response.data.toDomain()
                    .apply {
                        items.map {
                            val currentMetadata =
                                VideoListMetadata.getVideoMetadata(it.domainCameraFile.name)?.videoMetadata
                            it.domainVideoMetadata = currentMetadata
                        }
                    }

                if (domainInformationFileResponse.items.size < FileList.videoList.size) {
                    domainInformationFileResponse.items =
                        FileList.videoList.map {
                        val currentMetadata =
                            VideoListMetadata.getVideoMetadata(it.domainCameraFile.name)?.videoMetadata
                        DomainInformationFile(it.domainCameraFile, currentMetadata)
                    } as MutableList

                    return Result.Success(domainInformationFileResponse)
                }

                FileList.videoList = domainInformationFileResponse.items
                return Result.Success(domainInformationFileResponse)
            }
            is Result.Error -> Result.Error(RequestError.GET_LIST.getException())
        }
    }
}
