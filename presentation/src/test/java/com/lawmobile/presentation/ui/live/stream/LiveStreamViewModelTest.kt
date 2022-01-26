package com.lawmobile.presentation.ui.live.stream

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
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class LiveStreamViewModelTest {

    private val vlcMediaPlayer: VLCMediaPlayer = mockk {
        every { create(any(), any()) } just Runs
        every { play() } just Runs
        every { stop() } just Runs
    }

    private val liveStreamingUseCase: LiveStreamingUseCase = mockk {
        every { getUrlForLiveStream() } returns String()
    }

    private val liveStreamViewModel: LiveStreamViewModel by lazy {
        LiveStreamViewModel(vlcMediaPlayer, liveStreamingUseCase)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        liveStreamViewModel.getUrlLive()
        verify { liveStreamingUseCase.getUrlForLiveStream() }
    }

    @Test
    fun getMediaPlayer() {
        Assert.assertEquals(vlcMediaPlayer, liveStreamViewModel.mediaPlayer)
    }
}
