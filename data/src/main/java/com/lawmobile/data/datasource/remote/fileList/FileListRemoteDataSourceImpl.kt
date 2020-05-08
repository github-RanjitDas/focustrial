package com.lawmobile.data.datasource.remote.fileList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

class FileListRemoteDataSourceImpl(private val cameraConnectService: CameraConnectService) :
    FileListRemoteDataSource {
    override suspend fun getSnapshotList(): Result<List<CameraConnectFile>> =
        cameraConnectService.getListOfImages()

    override suspend fun getVideoList(): Result<List<CameraConnectFile>> =
        cameraConnectService.getListOfVideos()
}