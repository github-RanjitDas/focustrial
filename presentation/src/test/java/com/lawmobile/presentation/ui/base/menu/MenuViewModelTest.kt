package com.lawmobile.presentation.ui.base.menu

import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

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

    @Test
    fun testFlowDisconnectCameraFlow() {
        coEvery { liveStreamingUseCase.disconnectCamera() } returns Result.Success(Unit)
        runBlocking { menuViewModel.disconnectCamera() }
        coVerify { liveStreamingUseCase.disconnectCamera() }
    }

    @Test
    fun getPendingNotificationsCountFlow() {
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns Result.Success(1)
        runBlocking { menuViewModel.getPendingNotificationsCount() }
        coVerify { eventsUseCase.getPendingNotificationsCount() }
    }

    @Test
    fun getPendingNotificationsCountSuccess() {
        val result = Result.Success(1)
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns result
        menuViewModel.getPendingNotificationsCount()
        runBlocking {
            Assert.assertEquals(
                result,
                menuViewModel.pendingNotificationsCountResult.value
            )
        }
    }

    @Test
    fun getPendingNotificationsCountError() {
        val result = Result.Success(1)
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns result
        menuViewModel.getPendingNotificationsCount()
        runBlocking {
            Assert.assertEquals(
                result,
                menuViewModel.pendingNotificationsCountResult.value
            )
        }
    }
}
