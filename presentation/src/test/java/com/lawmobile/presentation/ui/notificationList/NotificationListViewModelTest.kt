package com.lawmobile.presentation.ui.notificationList

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
internal class NotificationListViewModelTest {

    private val eventsUseCase: EventsUseCase = mockk()

    private val notificationListViewModel by lazy {
        NotificationListViewModel(eventsUseCase)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun getNotificationListSuccess() = runBlockingTest {
        val result = Result.Success(
            listOf<CameraEvent>(mockk { every { eventType } returns EventType.NOTIFICATION })
        )
        coEvery { eventsUseCase.getNotificationEvents() } returns result
        coEvery { eventsUseCase.setAllNotificationsAsRead() } returns Unit

        notificationListViewModel.getNotificationEvents()
        Assert.assertEquals(result, notificationListViewModel.notificationEventsResult.value)
        Assert.assertEquals(0, CameraInfo.currentNotificationCount)

        coVerify {
            eventsUseCase.getNotificationEvents()
            eventsUseCase.setAllNotificationsAsRead()
        }
    }

    @Test
    fun getNotificationListError() = runBlockingTest {
        val result = Result.Error(mockk())
        coEvery { eventsUseCase.getNotificationEvents() } returns result
        notificationListViewModel.getNotificationEvents()
        Assert.assertEquals(result, notificationListViewModel.notificationEventsResult.value)
    }
}
