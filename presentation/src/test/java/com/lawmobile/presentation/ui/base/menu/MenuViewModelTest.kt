package com.lawmobile.presentation.ui.base.menu

import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class MenuViewModelTest {

    private val liveStreamingUseCase: LiveStreamingUseCase = mockk()
    private val eventsUseCase: EventsUseCase = mockk()

    private val menuViewModel: MenuViewModel by lazy {
        MenuViewModel(
            liveStreamingUseCase,
            eventsUseCase
        )
    }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testFlowDisconnectCameraFlow() = runBlockingTest {
        coEvery { liveStreamingUseCase.disconnectCamera() } returns Result.Success(Unit)
        menuViewModel.disconnectCamera()
        coVerify { liveStreamingUseCase.disconnectCamera() }
    }

    @Test
    fun getPendingNotificationsCountFlow() = runBlockingTest {
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns Result.Success(1)
        menuViewModel.getPendingNotificationsCount()
        coVerify { eventsUseCase.getPendingNotificationsCount() }
    }

    @Test
    fun getPendingNotificationsCountSuccess() = runBlockingTest {
        val result = Result.Success(1)
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns result
        menuViewModel.getPendingNotificationsCount()
        Assert.assertEquals(result, menuViewModel.pendingNotificationsCountResult.value)
    }

    @Test
    fun getPendingNotificationsCountError() = runBlockingTest {
        val result = Result.Success(1)
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns result
        menuViewModel.getPendingNotificationsCount()
        Assert.assertEquals(result, menuViewModel.pendingNotificationsCountResult.value)
    }
}
