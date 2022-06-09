package com.lawmobile.data.repository.file

import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.data.datasource.remote.file.FileRemoteDataSource
import com.lawmobile.data.mappers.impl.FileMapper.toDomain
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class FileRepositoryImplTest {

    private val fileRemoteDataSource: FileRemoteDataSource = mockk()

    private val fileRepositoryImpl by lazy {
        FileRepositoryImpl(fileRemoteDataSource)
    }

    @Test
    fun getFileBytes() {
        val byte = ByteArray(1)
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")
        coEvery { fileRemoteDataSource.getFileBytes(cameraConnectFile) } returns Result.Success(byte)
        runBlocking {
            val result =
                fileRepositoryImpl.getFileBytes(cameraConnectFile.toDomain()) as Result.Success
            Assert.assertEquals(result.data, byte)
        }
        coVerify { fileRemoteDataSource.getFileBytes(cameraConnectFile) }
    }

    @Test
    fun saveFailSafeVideo() {
        val byte = ByteArray(1)
        coEvery { fileRemoteDataSource.saveFailSafeVideo(byte) } returns Result.Success(Unit)
        runBlocking {
            val result =
                fileRepositoryImpl.saveFailSafeVideo(byte) as Result.Success
            Assert.assertEquals(result.data, Unit)
        }
        coVerify { fileRemoteDataSource.saveFailSafeVideo(byte) }
    }
}
