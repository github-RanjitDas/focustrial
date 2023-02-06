package com.lawmobile.data.datasource.remote.thumbnailList

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.body_cameras.entities.FileResponseWithErrors
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class ThumbnailListRemoteDataSourceImpl(cameraServiceFactory: CameraServiceFactory) :
    ThumbnailListRemoteDataSource {

    private var cameraService = cameraServiceFactory.create()

    override suspend fun getImageBytes(cameraFile: CameraFile): Result<ByteArray> =
        cameraService.getImageBytes(cameraFile)

    override suspend fun getSnapshotList(): Result<FileResponseWithErrors> =
        cameraService.getListOfImages()

    override suspend fun getVideoList(): Result<FileResponseWithErrors> {
        return cameraService.getListOfVideos()
    }
}
