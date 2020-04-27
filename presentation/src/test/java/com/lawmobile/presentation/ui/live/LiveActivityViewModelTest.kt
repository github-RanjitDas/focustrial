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
import kotlinx.coroutines.test.setMain
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
        coEvery { liveStreamingUseCase.startRecordVideo() } returns Result.Success(Unit)
        liveActivityViewModel.startRecordVideo()
        coVerify { liveStreamingUseCase.startRecordVideo() }
    }

    @Test
    fun testStopRecordVideoFlow() {
        coEvery { liveStreamingUseCase.stopRecordVideo() } returns Result.Success(Unit)
        liveActivityViewModel.stopRecordVideo()
        coVerify { liveStreamingUseCase.stopRecordVideo() }
    }


    @Test
    fun testTakePhotoFlow() {
        coEvery { liveStreamingUseCase.takePhoto() } returns Result.Success(Unit)
        liveActivityViewModel.takePhoto()
        coVerify { liveStreamingUseCase.takePhoto() }
    }

}