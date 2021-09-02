package com.lawmobile.presentation.ui.live.stream

import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.utils.VLCMediaPlayer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class LiveStreamViewModelTest {

    private val vlcMediaPlayer: VLCMediaPlayer = mockk(relaxed = true)

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
}
