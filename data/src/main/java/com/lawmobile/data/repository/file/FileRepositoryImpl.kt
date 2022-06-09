package com.lawmobile.data.repository.file

import com.lawmobile.data.datasource.remote.file.FileRemoteDataSource
import com.lawmobile.data.mappers.impl.FileMapper.toCamera
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.repository.file.FileRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class FileRepositoryImpl(
    private val fileRemoteDataSource: FileRemoteDataSource
) : FileRepository {
    override suspend fun getFileBytes(file: DomainCameraFile): Result<ByteArray> =
        fileRemoteDataSource.getFileBytes(file.toCamera())

    override suspend fun saveFailSafeVideo(fileBytes: ByteArray): Result<Unit> =
        fileRemoteDataSource.saveFailSafeVideo(fileBytes)
}
