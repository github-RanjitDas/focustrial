package com.lawmobile.domain.usecase.videoPlayback

import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VideoPlaybackUseCaseTest {

    private val videoPlaybackRepository: VideoPlaybackRepository = mockk()
    private val videoPlaybackUseCaseImpl by lazy {
        VideoPlaybackUseCaseImpl(videoPlaybackRepository)
    }

    @Test
    fun testGetInformationResourcesVideoSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { videoPlaybackRepository.getInformationResourcesVideo(cameraConnectFile) } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackUseCaseImpl.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify { videoPlaybackRepository.getInformationResourcesVideo(cameraConnectFile) }
    }

    @Test
    fun testGetInformationResourcesVideoError() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { videoPlaybackRepository.getInformationResourcesVideo(cameraConnectFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackUseCaseImpl.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { videoPlaybackRepository.getInformationResourcesVideo(cameraConnectFile) }
    }

    @Test
    fun testGetCatalogInfoSuccess() {
        coEvery { videoPlaybackRepository.getCatalogInfo() } returns Result.Success(mockk())
        runBlocking {
            val result = videoPlaybackUseCaseImpl.getCatalogInfo()
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { videoPlaybackRepository.getCatalogInfo() }
    }

    @Test
    fun testGetCatalogInfoError() {
        coEvery { videoPlaybackRepository.getCatalogInfo() } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result = videoPlaybackUseCaseImpl.getCatalogInfo()
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { videoPlaybackRepository.getCatalogInfo() }
    }
}