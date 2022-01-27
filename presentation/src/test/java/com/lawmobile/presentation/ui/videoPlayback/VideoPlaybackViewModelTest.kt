package com.lawmobile.presentation.ui.videoPlayback

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCase
import com.lawmobile.presentation.ui.videoPlayback.state.VideoPlaybackState
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class VideoPlaybackViewModelTest {

    private val videoPlaybackUseCase: VideoPlaybackUseCase = mockk()
    private val vlcMediaPlayer: VLCMediaPlayer = mockk()
    private val informationManager: VideoInformationManager = mockk()

    private val viewModel by lazy {
        VideoPlaybackViewModel(videoPlaybackUseCase, vlcMediaPlayer, informationManager)
    }

    private val dispatcher = TestCoroutineDispatcher()
    private val job by lazy { Job() }
    private val testScope by lazy { TestCoroutineScope() }

    @BeforeEach
    fun setUp() = Dispatchers.setMain(dispatcher)

    @AfterEach
    fun clean() = job.cancel()

    @Test
    fun testGetInformationResourcesVideoSuccess() = runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk()

        coEvery { videoPlaybackUseCase.getInformationResourcesVideo(any()) } returns
            Result.Success(mockk())

        viewModel.getMediaInformation(domainCameraFile)
        dispatcher.advanceTimeBy(250)
        testScope.launch {
            Assert.assertTrue(viewModel.mediaInformation.value is Result.Success)
        }
        coVerify { videoPlaybackUseCase.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testGetInformationResourcesVideoError() = runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk()
        coEvery { videoPlaybackUseCase.getInformationResourcesVideo(any()) } returns
            Result.Error(mockk())

        viewModel.getMediaInformation(domainCameraFile)
        dispatcher.advanceTimeBy(250)
        testScope.launch {
            Assert.assertTrue(viewModel.mediaInformation.value is Result.Error)
        }
        coVerify { videoPlaybackUseCase.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testGetVideoMetadataLiveDataSuccess() {
        coEvery {
            videoPlaybackUseCase.getVideoMetadata(any(), any())
        } returns Result.Success(mockk())
        testScope.launch {
            viewModel.getVideoInformation("", "")
            Assert.assertTrue(viewModel.videoInformation.value is Result.Success)
        }
        coVerify { videoPlaybackUseCase.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testGetVideoMetadataLiveDataError() {
        coEvery {
            videoPlaybackUseCase.getVideoMetadata(any(), any())
        } returns Result.Error(mockk())
        testScope.launch {
            viewModel.getVideoInformation("", "")
            Assert.assertTrue(viewModel.videoInformation.value is Result.Error)
        }
        coVerify { videoPlaybackUseCase.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testSaveVideoMetadataSuccess() {
        coEvery { videoPlaybackUseCase.saveVideoMetadata(any()) } returns Result.Success(mockk())
        testScope.launch {
            viewModel.saveVideoInformation(mockk())
            Assert.assertTrue(viewModel.updateMetadataResult.first() is Result.Success)
        }
        coVerify { videoPlaybackUseCase.saveVideoMetadata(any()) }
    }

    @Test
    fun testSaveVideoMetadataError() {
        coEvery { videoPlaybackUseCase.saveVideoMetadata(any()) } returns Result.Success(mockk())
        testScope.launch {
            viewModel.saveVideoInformation(mockk())
            Assert.assertTrue(viewModel.updateMetadataResult.first() is Result.Success)
        }
        coVerify { videoPlaybackUseCase.saveVideoMetadata(any()) }
    }

    @Test
    fun isAssociateDialogOpen() {
        Assert.assertFalse(viewModel.isAssociateDialogOpen)
    }

    @Test
    fun setAssociateDialogOpen() {
        viewModel.isAssociateDialogOpen = true
        Assert.assertTrue(viewModel.isAssociateDialogOpen)
    }

    @Test
    fun getState() {
        Assert.assertTrue(viewModel.getState() is VideoPlaybackState.Default)
    }

    @Test
    fun setState() {
        viewModel.setState(VideoPlaybackState.FullScreen)
        Assert.assertTrue(viewModel.getState() is VideoPlaybackState.FullScreen)
    }

    @Test
    fun getMediaPlayer() {
        Assert.assertEquals(vlcMediaPlayer, viewModel.mediaPlayer)
    }

    @Test
    fun getInformationManager() {
        Assert.assertEquals(informationManager, viewModel.informationManager)
    }
}
