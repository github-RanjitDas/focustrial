package com.lawmobile.data.repository.simpleList

import com.lawmobile.data.datasource.remote.simpleList.SimpleListRemoteDataSource
import com.lawmobile.data.mappers.impl.FileResponseMapper.toDomain
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.entities.VideoListMetadata
import com.lawmobile.domain.repository.simpleList.SimpleListRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class SimpleListRepositoryImpl(
    private val simpleListRemoteDataSource: SimpleListRemoteDataSource
) : SimpleListRepository {

    override suspend fun getSnapshotList(): Result<DomainInformationFileResponse> =
        when (val response = simpleListRemoteDataSource.getSnapshotList()) {
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
        return when (val response = simpleListRemoteDataSource.getVideoList()) {
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
            is Result.Error -> response
        }
    }

    override suspend fun getAudioList(): Result<DomainInformationFileResponse> =
        when (val response = simpleListRemoteDataSource.getAudioList()) {
            is Result.Success -> {
                val domainInformationFileResponse = response.data.toDomain()
                if (domainInformationFileResponse.items.size < FileList.audioList.size) {
                    domainInformationFileResponse.items = FileList.audioList as MutableList
                    Result.Success(domainInformationFileResponse)
                } else {
                    FileList.audioList = domainInformationFileResponse.items
                    Result.Success(domainInformationFileResponse)
                }
            }
            is Result.Error -> response
        }
}
