package com.lawmobile.domain.usecase.liveStreaming

import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LiveStreamingUseCaseImplTest {

    private val liveStreamingRepository: LiveStreamingRepository = mockk {
        every { getUrlForLiveStream() } returns String()
    }

    private val liveStreamingUseCaseImpl: LiveStreamingUseCaseImpl by lazy {
        LiveStreamingUseCaseImpl(liveStreamingRepository)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        liveStreamingUseCaseImpl.getUrlForLiveStream()
        verify { liveStreamingUseCaseImpl.getUrlForLiveStream() }
    }
}