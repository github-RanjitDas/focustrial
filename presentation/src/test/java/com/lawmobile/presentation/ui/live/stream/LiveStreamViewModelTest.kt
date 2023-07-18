package com.lawmobile.presentation.ui.live.stream

import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class LiveStreamViewModelTest {

    private val liveStreamingUseCase: LiveStreamingUseCase = mockk {
        every { getUrlForLiveStream() } returns String()
    }

    private val liveStreamViewModel: LiveStreamViewModel by lazy {
        LiveStreamViewModel(liveStreamingUseCase)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        liveStreamViewModel.getUrlLive()
        verify { liveStreamingUseCase.getUrlForLiveStream() }
    }
}
