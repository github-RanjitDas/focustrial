package com.lawmobile.data.datasource.remote.file

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class FileRemoteDataSourceImpl(
    private val bodyCameraFactory: CameraServiceFactory
) : FileRemoteDataSource {

    private val bodyCamera by lazy { bodyCameraFactory.create() }

    override suspend fun getFileBytes(file: CameraFile): Result<ByteArray> =
        bodyCamera.getFileBytes(file)

    override suspend fun saveFailSafeVideo(fileBytes: ByteArray): Result<Unit> =
        bodyCamera.saveFailSafeVideo(fileBytes)
}
