package com.lawmobile.data.repository.simpleList

import com.lawmobile.data.datasource.remote.simpleList.SimpleListRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.lawmobile.domain.repository.simpleList.SimpleListRepository
import com.safefleet.mobile.commons.helpers.Result

class SimpleListRepositoryImpl(private val simpleListRemoteDataSource: SimpleListRemoteDataSource) :
    SimpleListRepository {

    override suspend fun getSnapshotList(): Result<DomainInformationFileResponse> {
        return when (val response = simpleListRemoteDataSource.getSnapshotList()) {
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

    override suspend fun getVideoList(): Result<DomainInformationFileResponse> {
        return when (val response = simpleListRemoteDataSource.getVideoList()) {
            is Result.Success -> {
                val domainInformationFileResponse = DomainInformationFileResponse()
                val items = response.data.items.sortedByDescending { it.date }.map {
                    val remoteMetadata = VideoListMetadata.getVideoMetadata(it.name)
                    DomainInformationFile(it, remoteMetadata?.videoMetadata)
                }
                domainInformationFileResponse.errors.addAll(response.data.errors)
                if (items.size < FileList.listOfVideos.size) {
                    domainInformationFileResponse.listItems.addAll(FileList.listOfVideos.map {
                        val remoteMetadata =
                            VideoListMetadata.getVideoMetadata(it.cameraConnectFile.name)
                        DomainInformationFile(
                            it.cameraConnectFile,
                            remoteMetadata?.videoMetadata
                        )
                    })
                    return Result.Success(domainInformationFileResponse)
                }

                FileList.changeListOfVideos(items)
                domainInformationFileResponse.listItems.addAll(items)
                return Result.Success(domainInformationFileResponse)
            }
            is Result.Error -> response
        }
    }
}