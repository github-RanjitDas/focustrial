package com.lawmobile.data.repository.simpleList

import com.lawmobile.data.datasource.remote.simpleList.SimpleListRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.data.mappers.FileResponseMapper
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.repository.simpleList.SimpleListRepository
import com.safefleet.mobile.commons.helpers.Result

class SimpleListRepositoryImpl(private val simpleListRemoteDataSource: SimpleListRemoteDataSource) :
    SimpleListRepository {

    override suspend fun getSnapshotList(): Result<DomainInformationFileResponse> =
        when (val response = simpleListRemoteDataSource.getSnapshotList()) {
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

    override suspend fun getVideoList(): Result<DomainInformationFileResponse> {
        return when (val response = simpleListRemoteDataSource.getVideoList()) {
            is Result.Success -> {
                val domainInformationFileResponse =
                    FileResponseMapper.cameraToDomain(response.data)
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

                FileList.changeVideoList(domainInformationFileResponse.items)
                return Result.Success(domainInformationFileResponse)
            }
            is Result.Error -> response
        }
    }
}