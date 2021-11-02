package com.lawmobile.presentation.ui.base

import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
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
internal class BaseViewModelTest {

    private val eventsUseCase: EventsUseCase = mockk()

    private val baseViewModel: BaseViewModel by lazy { BaseViewModel() }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun waitToFinish() = runBlockingTest {
        baseViewModel.waitToFinish(100)
        dispatcher.advanceTimeBy(101)
        Assert.assertTrue(baseViewModel.isWaitFinishedLiveData.value!!.getContent())
    }

    @Test
    fun getLoadingTimeout() {
        val timeout = 70000L
        Assert.assertEquals(timeout, BaseViewModel.getLoadingTimeOut())
    }

    @Test
    fun saveNotificationEventFlow() = runBlockingTest {
        coEvery { eventsUseCase.saveEvent(any()) } just Runs
        baseViewModel.setEventsUseCase(eventsUseCase)
        baseViewModel.saveNotificationEvent(mockk())
        coVerify { eventsUseCase.saveEvent(any()) }
    }
}
