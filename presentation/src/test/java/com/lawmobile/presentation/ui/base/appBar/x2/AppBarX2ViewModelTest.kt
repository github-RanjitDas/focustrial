package com.lawmobile.presentation.ui.base.appBar.x2

import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class AppBarX2ViewModelTest {

    private val eventsUseCase: EventsUseCase = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val appBarX2ViewModel: AppBarX2ViewModel by lazy {
        AppBarX2ViewModel(eventsUseCase, dispatcher)
    }

    @Before
    fun setup() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun getNumberOfPendingNotificationFlow() = runTest(dispatcher) {
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns Result.Success(1)
        appBarX2ViewModel.getUnreadNotificationCount()
        coVerify { eventsUseCase.getPendingNotificationsCount() }
    }

    @Test
    fun getNumberOfPendingNotificationSuccess() = runTest(dispatcher) {
        val result = Result.Success(1)
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns result
        appBarX2ViewModel.getUnreadNotificationCount()
        Assert.assertEquals(
            result,
            appBarX2ViewModel.pendingNotificationCountResult.value
        )
    }

    @Test
    fun getNumberOfPendingNotificationError() = runTest(dispatcher) {
        val result = Result.Error(Exception())
        coEvery { eventsUseCase.getPendingNotificationsCount() } returns result
        appBarX2ViewModel.getUnreadNotificationCount()
        Assert.assertEquals(
            result,
            appBarX2ViewModel.pendingNotificationCountResult.value
        )
    }
}
