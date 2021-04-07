package com.lawmobile.presentation.ui.live.appBar.x2

import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class LiveAppBarX2ViewModelTest {

    private val eventsUseCase: EventsUseCase = mockk()

    private val liveAppBarX2ViewModel: LiveAppBarX2ViewModel by lazy {
        LiveAppBarX2ViewModel(eventsUseCase)
    }

    @Test
    fun getNumberOfPendingNotificationFlow() {
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns Result.Success(1)
        liveAppBarX2ViewModel.getPendingNotificationsCount()
        coVerify { eventsUseCase.getPendingNotificationsCount() }
    }

    @Test
    fun getNumberOfPendingNotificationSuccess() {
        val result = Result.Success(1)
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns result
        liveAppBarX2ViewModel.getPendingNotificationsCount()
        Assert.assertEquals(
            result,
            liveAppBarX2ViewModel.pendingNotificationSizeResult.value
        )
    }

    @Test
    fun getNumberOfPendingNotificationError() {
        val result = Result.Error(Exception())
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns result
        liveAppBarX2ViewModel.getPendingNotificationsCount()
        Assert.assertEquals(
            result,
            liveAppBarX2ViewModel.pendingNotificationSizeResult.value
        )
    }
}
