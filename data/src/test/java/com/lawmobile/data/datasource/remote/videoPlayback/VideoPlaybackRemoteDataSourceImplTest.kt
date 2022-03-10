package com.lawmobile.data.datasource.remote.videoPlayback

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
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VideoPlaybackRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }
    private val videoPlaybackRemoteDataSourceImpl by lazy {
        VideoPlaybackRemoteDataSourceImpl(cameraServiceFactory)
    }

    @Test
    fun testGetInformationResourcesVideoSuccess() {
        val cameraConnectFile: CameraFile = mockk()
        coEvery { cameraService.getInformationResourcesVideo(cameraConnectFile) } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.getInformationResourcesVideo(cameraConnectFile) }
    }

    @Test
    fun testGetInformationResourcesVideoError() {
        val cameraConnectFile: CameraFile = mockk()
        coEvery { cameraService.getInformationResourcesVideo(cameraConnectFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { cameraService.getInformationResourcesVideo(cameraConnectFile) }
    }

    @Test
    fun testGetVideoMetadataSuccess() {
        coEvery { cameraService.getVideoMetadata(any(), any()) } returns Result.Success(mockk())
        runBlocking {
            val result = videoPlaybackRemoteDataSourceImpl.getVideoMetadata("", "")
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testGetVideoMetadataError() {
        coEvery { cameraService.getVideoMetadata(any(), any()) } returns Result.Error(mockk())
        runBlocking {
            val result = videoPlaybackRemoteDataSourceImpl.getVideoMetadata("", "")
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { cameraService.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testSaveVideoMetadataSuccess() {
        coEvery { cameraService.saveVideoMetadata(any()) } returns Result.Success(mockk())
        runBlocking {
            val result = videoPlaybackRemoteDataSourceImpl.saveVideoMetadata(mockk())
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.saveVideoMetadata(any()) }
    }

    @Test
    fun testSaveVideoMetadataError() {
        coEvery { cameraService.saveVideoMetadata(any()) } returns Result.Error(mockk())
        runBlocking {
            val result = videoPlaybackRemoteDataSourceImpl.saveVideoMetadata(mockk())
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { cameraService.saveVideoMetadata(any()) }
    }
}
