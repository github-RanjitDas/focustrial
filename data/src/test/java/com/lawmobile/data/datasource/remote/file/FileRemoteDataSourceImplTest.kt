package com.lawmobile.data.datasource.remote.file

import com.lawmobile.body_cameras.CameraService
import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class FileRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }
    private val fileRemoteDataSourceImpl by lazy {
        FileRemoteDataSourceImpl(cameraServiceFactory)
    }

    @Test
    fun getFileBytes() {
        val cameraFile: CameraFile = mockk()
        val byte = ByteArray(1)

        coEvery { cameraService.getFileBytes(cameraFile) } returns Result.Success(byte)
        runBlocking {
            val result = fileRemoteDataSourceImpl.getFileBytes(cameraFile)
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.getFileBytes(cameraFile) }
    }

    @Test
    fun saveFailSafeVideo() {
        val byte = ByteArray(1)

        coEvery { cameraService.saveFailSafeVideo(byte) } returns Result.Success(Unit)
        runBlocking {
            val result = fileRemoteDataSourceImpl.saveFailSafeVideo(byte)
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.saveFailSafeVideo(byte) }
    }
}
