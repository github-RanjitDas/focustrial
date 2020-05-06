package com.lawmobile.data.datasource.remote.fileList

import com.safefleet.mobile.avml.cameras.x1.CameraDataSource
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result

class FileListRemoteDataSourceImpl(private val cameraDataSource: CameraDataSource) :
    FileListRemoteDataSource {
    override suspend fun getSnapshotList(): Result<List<CameraConnectFile>> =
        cameraDataSource.getListOfImages()

    override suspend fun getVideoList(): Result<List<CameraConnectFile>> =
        cameraDataSource.getListOfVideos()
}