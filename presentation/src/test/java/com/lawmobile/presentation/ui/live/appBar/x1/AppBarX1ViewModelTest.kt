package com.lawmobile.presentation.ui.live.appBar.x1

import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class AppBarX1ViewModelTest {

    private val liveStreamingUseCase: LiveStreamingUseCase = mockk()

    private val appBarX1ViewModel: AppBarX1ViewModel by lazy {
        AppBarX1ViewModel(liveStreamingUseCase)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun disconnectCameraFlow() {
        coEvery { liveStreamingUseCase.disconnectCamera() } returns Result.Success(Unit)
        appBarX1ViewModel.disconnectCamera()
        coVerify { liveStreamingUseCase.disconnectCamera() }
    }
}
