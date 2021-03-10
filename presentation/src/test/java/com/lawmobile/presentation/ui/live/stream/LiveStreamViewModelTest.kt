package com.lawmobile.presentation.ui.live.stream

import android.view.SurfaceView
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.VLCMediaPlayer
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class LiveStreamViewModelTest {

    private val vlcMediaPlayer: VLCMediaPlayer = mockk {
        every { createMediaPlayer(any(), any()) } just Runs
        every { setSizeInMediaPlayer(any()) } just Runs
        every { playMediaPlayer() } just Runs
        every { stopMediaPlayer() } just Runs
    }

    private val liveStreamingUseCase: LiveStreamingUseCase = mockk {
        every { getUrlForLiveStream() } returns String()
    }

    private val liveStreamViewModel: LiveStreamViewModel by lazy {
        LiveStreamViewModel(vlcMediaPlayer, liveStreamingUseCase)
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        liveStreamViewModel.getUrlLive()
        verify { liveStreamingUseCase.getUrlForLiveStream() }
    }

    @Test
    fun testCreateVLCMediaPlayerVerifyFlow() {
        val surfaceView = mockk<SurfaceView>()
        liveStreamViewModel.createVLCMediaPlayer("", surfaceView)
        verify { liveStreamViewModel.createVLCMediaPlayer(any(), any()) }
    }

    @Test
    fun testStartVLCMediaPlayerVerifyFlow() {
        liveStreamViewModel.startVLCMediaPlayer()
        verify { liveStreamViewModel.startVLCMediaPlayer() }
    }

    @Test
    fun testStopVLCMediaPlayerVerifyFlow() {
        liveStreamViewModel.stopVLCMediaPlayer()
        verify { liveStreamViewModel.stopVLCMediaPlayer() }
    }
}
