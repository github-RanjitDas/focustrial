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

    private val errors = mutableListOf<String>()
    private lateinit var snapshotFile: CameraConnectFile

    override suspend fun getImageByteList(currentPage: Int): Result<List<DomainInformationImage>> {
        val items = mutableListOf<DomainInformationImage>()

        val listResult = getSnapshotList()
        if (listResult is Result.Error) return listResult

        imageFlow(currentPage).collect { result ->
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

    private suspend fun getSnapshotList(): Result<Unit> {
        return if (FileList.listOfImages.isEmpty()) {
            return when (val response = thumbnailListRemoteDataSource.getSnapshotList()) {
                is Result.Success -> {
                    if (response.data.items.size > FileList.listOfImages.size)
                        FileList.listOfImages =
                            response.data.items.sortedByDescending { it.getCreationDate() }.map {
                                DomainInformationFile(it, null)
                            }
                    Result.Success(Unit)
                }
                is Result.Error -> Result.Error(Exception("Error retrieving image list"))
            }
        } else Result.Success(Unit)
    }

    override fun getImageListSize(): Int = FileList.listOfImages.size

    private fun imageFlow(currentPage: Int) = flow {
        for (i in IMAGES_PER_PAGE * (currentPage - 1) until IMAGES_PER_PAGE * currentPage) {
            if (isNotLastSnapshot(i)) {
                FileList.listOfImages[i].run {
                    delay(370)
                    cameraConnectFile.let {
                        snapshotFile = it
                        emit(thumbnailListRemoteDataSource.getImageBytes(it))
                    }
                }
            }
        }
    }

    private fun isNotLastSnapshot(i: Int) = i < FileList.listOfImages.size

    companion object {
        const val IMAGES_PER_PAGE = 2
    }
}