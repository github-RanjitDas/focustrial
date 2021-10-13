package com.lawmobile.presentation.ui.videoPlayback

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class VideoPlaybackViewModelTest {

    private val videoPlaybackUseCase: VideoPlaybackUseCase = mockk()
    private val vlcMediaPlayer: VLCMediaPlayer = mockk()

    private val videoPlaybackViewModel by lazy {
        VideoPlaybackViewModel(videoPlaybackUseCase, vlcMediaPlayer)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetInformationResourcesVideoSuccess() = runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk()

        coEvery { videoPlaybackUseCase.getInformationResourcesVideo(any()) } returns
            Result.Success(mockk())

        videoPlaybackViewModel.getInformationOfVideo(domainCameraFile)
        dispatcher.advanceTimeBy(250)
        Assert.assertTrue(videoPlaybackViewModel.domainInformationVideoLiveData.value is Result.Success)

        coVerify { videoPlaybackUseCase.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testGetInformationResourcesVideoError() = runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk()
        coEvery { videoPlaybackUseCase.getInformationResourcesVideo(any()) } returns
            Result.Error(mockk())

        videoPlaybackViewModel.getInformationOfVideo(domainCameraFile)
        dispatcher.advanceTimeBy(250)
        Assert.assertTrue(videoPlaybackViewModel.domainInformationVideoLiveData.value is Result.Error)

        coVerify { videoPlaybackUseCase.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testGetVideoMetadataLiveDataSuccess() {
        coEvery {
            videoPlaybackUseCase.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(mockk())
        runBlocking {
            videoPlaybackViewModel.getVideoMetadata("", "")
            Assert.assertTrue(videoPlaybackViewModel.videoMetadataLiveData.value is Result.Success)
        }
        coVerify { videoPlaybackUseCase.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testGetVideoMetadataLiveDataError() {
        coEvery {
            videoPlaybackUseCase.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Error(mockk())
        runBlocking {
            videoPlaybackViewModel.getVideoMetadata("", "")
            Assert.assertTrue(videoPlaybackViewModel.videoMetadataLiveData.value is Result.Error)
        }
        coVerify { videoPlaybackUseCase.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testSaveVideoMetadataSuccess() {
        coEvery { videoPlaybackUseCase.saveVideoMetadata(any()) } returns Result.Success(mockk())
        runBlocking {
            videoPlaybackViewModel.saveVideoMetadata(mockk())
            Assert.assertTrue(videoPlaybackViewModel.saveVideoMetadataLiveData.value is Result.Success)
        }
        coVerify { videoPlaybackUseCase.saveVideoMetadata(any()) }
    }

    @Test
    fun testSaveVideoMetadataError() {
        coEvery { videoPlaybackUseCase.saveVideoMetadata(any()) } returns Result.Success(mockk())
        runBlocking {
            videoPlaybackViewModel.saveVideoMetadata(mockk())
            Assert.assertTrue(videoPlaybackViewModel.saveVideoMetadataLiveData.value is Result.Success)
        }
        coVerify { videoPlaybackUseCase.saveVideoMetadata(any()) }
    }
}
