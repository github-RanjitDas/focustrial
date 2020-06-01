package com.lawmobile.data.datasource.remote.videoPlayback

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VideoPlaybackRemoteDataSourceImplTest {

    private val cameraConnectService: CameraConnectService = mockk()
    private val videoPlaybackRemoteDataSourceImpl by lazy {
        VideoPlaybackRemoteDataSourceImpl(cameraConnectService)
    }

    @Test
    fun testGetInformationResourcesVideoSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { cameraConnectService.getInformationResourcesVideo(cameraConnectFile) } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify { cameraConnectService.getInformationResourcesVideo(cameraConnectFile) }
    }

    @Test
    fun testGetInformationResourcesVideoError() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { cameraConnectService.getInformationResourcesVideo(cameraConnectFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { cameraConnectService.getInformationResourcesVideo(cameraConnectFile) }
    }

    @Test
    fun testGetVideoMetadataSuccess() {
        coEvery { cameraConnectService.getVideoMetadata(any()) } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.getVideoMetadata("")
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraConnectService.getVideoMetadata(any()) }
    }

    @Test
    fun testGetVideoMetadataError() {
        coEvery { cameraConnectService.getVideoMetadata(any()) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.getVideoMetadata("")
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { cameraConnectService.getVideoMetadata(any()) }
    }

    @Test
    fun testSaveVideoMetadataSuccess() {
        coEvery { cameraConnectService.saveVideoMetadata(any()) } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.saveVideoMetadata(mockk())
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraConnectService.saveVideoMetadata(any()) }
    }

    @Test
    fun testSaveVideoMetadataError() {
        coEvery { cameraConnectService.saveVideoMetadata(any()) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.saveVideoMetadata(mockk())
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { cameraConnectService.saveVideoMetadata(any()) }
    }
}