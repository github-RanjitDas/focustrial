package com.lawmobile.presentation.ui.live

import android.media.MediaActionSound
import android.view.SurfaceView
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.VLCMediaPlayer
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
class LiveActivityViewModelTest {

    private val vlcMediaPlayer: VLCMediaPlayer = mockk {
        every { createMediaPlayer(any(), any()) } just Runs
        every { setSizeInMediaPlayer(any()) } just Runs
        every { playMediaPlayer() } just Runs
        every { stopMediaPlayer() } just Runs
    }

    private val mediaActionSound: MediaActionSound = mockk()

    private val liveStreamingUseCase: LiveStreamingUseCase = mockk {
        every { getUrlForLiveStream() } returns String()
    }

    private val liveActivityViewModel: LiveActivityViewModel by lazy {
        LiveActivityViewModel(vlcMediaPlayer, liveStreamingUseCase, mediaActionSound)
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        liveActivityViewModel.getUrlLive()
        verify { liveStreamingUseCase.getUrlForLiveStream() }
    }

    @Test
    fun testCreateVLCMediaPlayerVerifyFlow() {
        val surfaceView = mockk<SurfaceView>()
        liveActivityViewModel.createVLCMediaPlayer("", surfaceView)
        verify { liveActivityViewModel.createVLCMediaPlayer(any(), any()) }
    }

    @Test
    fun testStartVLCMediaPlayerVerifyFlow() {
        liveActivityViewModel.startVLCMediaPlayer()
        verify { liveActivityViewModel.startVLCMediaPlayer() }
    }

    @Test
    fun testStopVLCMediaPlayerVerifyFlow() {
        liveActivityViewModel.stopVLCMediaPlayer()
        verify { liveActivityViewModel.stopVLCMediaPlayer() }
    }

    @Test
    fun testStartRecordVideoFlow() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingUseCase.startRecordVideo() } returns result
        liveActivityViewModel.startRecordVideo()
        Assert.assertEquals(liveActivityViewModel.startRecordVideo.value, result)
        coVerify { liveStreamingUseCase.startRecordVideo() }
    }

    @Test
    fun testStopRecordVideoFlow() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingUseCase.stopRecordVideo() } returns result
        liveActivityViewModel.stopRecordVideo()
        Assert.assertEquals(liveActivityViewModel.stopRecordVideo.value, result)
        coVerify { liveStreamingUseCase.stopRecordVideo() }
    }


    @Test
    fun testTakePhotoFlow() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingUseCase.takePhoto() } returns result
        liveActivityViewModel.takePhoto()
        Assert.assertEquals(liveActivityViewModel.resultTakePhotoLiveData.value, result)
        coVerify { liveStreamingUseCase.takePhoto() }

    }

    @Test
    fun playSoundTakePhoto() {
        every { mediaActionSound.play(any()) } just Runs
        liveActivityViewModel.playSoundTakePhoto()
        verify { mediaActionSound.play(MediaActionSound.SHUTTER_CLICK) }
    }

    @Test
    fun testGetCatalogInfoSuccess() {
        coEvery { liveStreamingUseCase.getCatalogInfo() } returns Result.Success(
            mockk()
        )
        runBlocking {
            liveActivityViewModel.getCatalogInfo()
            Assert.assertTrue(liveActivityViewModel.catalogInfoLiveData.value is Result.Success)
        }
        coVerify { liveStreamingUseCase.getCatalogInfo() }
    }

    @Test
    fun testGetCatalogInfoError() {
        coEvery { liveStreamingUseCase.getCatalogInfo() } returns Result.Error(
            mockk()
        )
        runBlocking {
            liveActivityViewModel.getCatalogInfo()
            Assert.assertTrue(liveActivityViewModel.catalogInfoLiveData.value is Result.Error)
        }
        coVerify { liveStreamingUseCase.getCatalogInfo() }
    }

}