package com.lawmobile.domain.usecase.videoPlayback

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.repository.videoPlayback.VideoPlaybackRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VideoPlaybackUseCaseImplTest {

    private val videoPlaybackRepository: VideoPlaybackRepository = mockk()
    private val videoPlaybackUseCaseImpl by lazy {
        VideoPlaybackUseCaseImpl(videoPlaybackRepository)
    }

    @Test
    fun testGetInformationResourcesVideoSuccess() {
        val domainCameraFile: DomainCameraFile = mockk()
        coEvery { videoPlaybackRepository.getInformationResourcesVideo(domainCameraFile) } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackUseCaseImpl.getInformationResourcesVideo(domainCameraFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify { videoPlaybackRepository.getInformationResourcesVideo(domainCameraFile) }
    }

    @Test
    fun testGetInformationResourcesVideoError() {
        val domainCameraFile: DomainCameraFile = mockk()
        coEvery { videoPlaybackRepository.getInformationResourcesVideo(domainCameraFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackUseCaseImpl.getInformationResourcesVideo(domainCameraFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { videoPlaybackRepository.getInformationResourcesVideo(domainCameraFile) }
    }

    @Test
    fun testGetVideoMetadataSuccess() {
        coEvery { videoPlaybackRepository.getVideoMetadata(any(), any()) } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackUseCaseImpl.getVideoMetadata("", "")
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { videoPlaybackRepository.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testGetVideoMetadataError() {
        coEvery { videoPlaybackRepository.getVideoMetadata(any(), any()) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackUseCaseImpl.getVideoMetadata("", "")
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { videoPlaybackRepository.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testSaveVideoMetadataSuccess() {
        coEvery { videoPlaybackRepository.saveVideoMetadata(any()) } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackUseCaseImpl.saveVideoMetadata(mockk())
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { videoPlaybackRepository.saveVideoMetadata(any()) }
    }

    @Test
    fun testSaveVideoMetadataError() {
        coEvery { videoPlaybackRepository.saveVideoMetadata(any()) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackUseCaseImpl.saveVideoMetadata(mockk())
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { videoPlaybackRepository.saveVideoMetadata(any()) }
    }
}
