package com.lawmobile.presentation.ui.live

import android.media.MediaActionSound
import android.view.SurfaceView
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.VLCMediaPlayer
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class LiveActivityViewModelTest {

    private val vlcMediaPlayer: VLCMediaPlayer = mockk{
        every { createMediaPlayer(any(), any()) } just Runs
        every { setSizeInMediaPlayer(any()) } just Runs
        every { playMediaPlayer() } just Runs
        every { stopMediaPlayer() } just Runs
    }

    private val mediaActionSound: MediaActionSound = mockk()

    private val liveStreamingUseCase: LiveStreamingUseCase = mockk{
        every { getUrlForLiveStream() } returns String()
    }

    private val liveActivityViewModel: LiveActivityViewModel by lazy {
        LiveActivityViewModel(vlcMediaPlayer, liveStreamingUseCase, mediaActionSound)
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

}