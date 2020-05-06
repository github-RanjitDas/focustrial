package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.domain.repository.fileList.FileListRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class FileListRepositoryImpl(private val fileListRemoteDataSource: FileListRemoteDataSource) :
    FileListRepository {
    override suspend fun getSnapshotList(): Result<List<CameraConnectFile>> =
        fileListRemoteDataSource.getSnapshotList()

    override suspend fun getVideoList(): Result<List<CameraConnectFile>> =
        fileListRemoteDataSource.getVideoList()
}