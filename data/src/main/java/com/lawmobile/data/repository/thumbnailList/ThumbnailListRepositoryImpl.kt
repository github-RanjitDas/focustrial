package com.lawmobile.data.repository.thumbnailList

import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationImage
import com.lawmobile.domain.extensions.getCreationDate
import com.lawmobile.domain.repository.thumbnailList.ThumbnailListRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class ThumbnailListRepositoryImpl(
    private val thumbnailListRemoteDataSource: ThumbnailListRemoteDataSource
) : ThumbnailListRepository {

    private lateinit var snapshotFile: CameraConnectFile

    override suspend fun getImageByteList(cameraConnectFile: CameraConnectFile): Result<List<DomainInformationImage>> {
        val errors = mutableListOf<String>()
        val items = mutableListOf<DomainInformationImage>()

        imageFlow(cameraConnectFile).collect { result ->
            when (result) {
                is Result.Success -> items.add(
                    DomainInformationImage(
                        snapshotFile,
                        result.data,
                        false
                    )
                )
                is Result.Error -> errors.add(snapshotFile.name)
            }
        }

        return if (errors.isEmpty()) Result.Success(items)
        else Result.Error(Exception("Error retrieving image of files: $errors"))
    }

    override suspend fun getImageList(): Result<List<DomainInformationFile>> {
        when (val response = thumbnailListRemoteDataSource.getSnapshotList()) {
            is Result.Success -> {
                if (response.data.items.size > FileList.listOfImages.size) {
                    FileList.listOfImages =
                        response.data.items.sortedByDescending { it.getCreationDate() }.map {
                            DomainInformationFile(it, null)
                        }
                }

                return Result.Success(FileList.listOfImages)
            }
            is Result.Error -> {
                if (FileList.listOfImages.isEmpty()) {
                    return Result.Error(Exception("Error retrieving image list"))
                }
                return Result.Success(FileList.listOfImages)
            }
        }
    }

    private fun imageFlow(cameraConnectFile: CameraConnectFile) = flow {
        delay(300)
        cameraConnectFile.let {
            snapshotFile = it
            emit(thumbnailListRemoteDataSource.getImageBytes(it))
        }
    }


}