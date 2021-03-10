package com.lawmobile.presentation.ui.live.appBar.x1

import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class LiveAppBarX1ViewModelTest {

    private val liveStreamingUseCase: LiveStreamingUseCase = mockk()

    private val liveAppBarX1ViewModel: LiveAppBarX1ViewModel by lazy {
        LiveAppBarX1ViewModel(liveStreamingUseCase)
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun disconnectCameraFlow() {
        coEvery { liveStreamingUseCase.disconnectCamera() } returns Result.Success(Unit)
        runBlocking { liveAppBarX1ViewModel.disconnectCamera() }
        coVerify { liveStreamingUseCase.disconnectCamera() }
    }
}
