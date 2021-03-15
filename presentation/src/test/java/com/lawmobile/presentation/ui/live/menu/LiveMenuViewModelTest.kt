package com.lawmobile.presentation.ui.live.menu

import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class LiveMenuViewModelTest {

    private val liveStreamingUseCase: LiveStreamingUseCase = mockk()
    private val menuLiveViewModel: LiveMenuViewModel by lazy {
        LiveMenuViewModel(
            liveStreamingUseCase
        )
    }

    @Test
    fun testFlowDisconnectCamera() {
        coEvery { liveStreamingUseCase.disconnectCamera() } returns Result.Success(Unit)
        runBlocking { menuLiveViewModel.disconnectCamera() }
        coVerify { liveStreamingUseCase.disconnectCamera() }
    }
}
