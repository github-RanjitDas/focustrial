package com.lawmobile.presentation.ui.videoPlayback

import android.view.SurfaceView
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class VideoPlaybackViewModelTest {

    private val videoPlaybackUseCase: VideoPlaybackUseCase = mockk()
    private val vlcMediaPlayer: VLCMediaPlayer = mockk()

    private val videoPlaybackViewModel by lazy {
        VideoPlaybackViewModel(videoPlaybackUseCase, vlcMediaPlayer)
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }


    @Test
    fun testGetInformationResourcesVideoSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { videoPlaybackUseCase.getInformationResourcesVideo(cameraConnectFile) } returns Result.Success(
            mockk()
        )
        runBlocking {
            videoPlaybackViewModel.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(videoPlaybackViewModel.domainInformationVideoLiveData.value is Result.Success)
        }

        coVerify { videoPlaybackUseCase.getInformationResourcesVideo(cameraConnectFile) }
    }

    @Test
    fun testGetInformationResourcesVideoError() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { videoPlaybackUseCase.getInformationResourcesVideo(cameraConnectFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            videoPlaybackViewModel.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(videoPlaybackViewModel.domainInformationVideoLiveData.value is Result.Error)
        }

        coVerify { videoPlaybackUseCase.getInformationResourcesVideo(cameraConnectFile) }
    }

    @Test
    fun testCreateVLCMediaPlayer() {
        val surfaceView = mockk<SurfaceView>()
        every { vlcMediaPlayer.createMediaPlayer(any(), any()) } just Runs
        videoPlaybackViewModel.createVLCMediaPlayer("", surfaceView)
        verify { vlcMediaPlayer.createMediaPlayer("", surfaceView) }
    }

    @Test
    fun testPlayVLCMediaPlayer() {
        every { vlcMediaPlayer.playMediaPlayer() } just Runs
        videoPlaybackViewModel.playVLCMediaPlayer()
        verify { vlcMediaPlayer.playMediaPlayer() }
    }

    @Test
    fun testPauseVLCMediaPlayer() {
        every { vlcMediaPlayer.pauseMediaPlayer() } just Runs
        videoPlaybackViewModel.pauseMediaPlayer()
        verify { vlcMediaPlayer.pauseMediaPlayer() }
    }

    @Test
    fun testStopVLCMediaPlayer() {
        every { vlcMediaPlayer.stopMediaPlayer() } just Runs
        videoPlaybackViewModel.stopMediaPlayer()
        verify { vlcMediaPlayer.stopMediaPlayer() }
    }

    @Test
    fun testChangeAspectRatio() {
        every { vlcMediaPlayer.changeAspectRatio() } just Runs
        videoPlaybackViewModel.changeAspectRatio()
        verify { vlcMediaPlayer.changeAspectRatio() }
    }

    @Test
    fun testIsMediaPlayerPayingTrue() {
        every { vlcMediaPlayer.isMediaPlayerPlaying() } returns true
        Assert.assertTrue(videoPlaybackViewModel.isMediaPlayerPlaying())
    }

    @Test
    fun testIsMediaPlayerPayingFalse() {
        every { vlcMediaPlayer.isMediaPlayerPlaying() } returns false
        Assert.assertFalse(videoPlaybackViewModel.isMediaPlayerPlaying())
    }

    @Test
    fun testGetTimeInMillisMediaPlayer() {
        every { vlcMediaPlayer.getTimeInMillisMediaPlayer() } returns 1000L
        videoPlaybackViewModel.getTimeInMillisMediaPlayer()
        Assert.assertEquals(1000L, videoPlaybackViewModel.currentTimeVideo.value)
        verify { vlcMediaPlayer.getTimeInMillisMediaPlayer() }
    }

    @Test
    fun testSetProgressMediaPlayer() {
        every { vlcMediaPlayer.setProgressMediaPlayer(any()) } just Runs
        videoPlaybackViewModel.setProgressMediaPlayer(100F)
        verify { vlcMediaPlayer.setProgressMediaPlayer(100F) }
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