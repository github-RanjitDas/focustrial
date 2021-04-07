package com.lawmobile.presentation.ui.notificationList

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class NotificationListViewModelTest {

    private val eventsUseCase: EventsUseCase = mockk()

    private val notificationListViewModel by lazy {
        NotificationListViewModel(eventsUseCase)
    }

    @Test
    fun getNotificationListSuccess() {
        val result = Result.Success(
            listOf<CameraEvent>(mockk { every { eventType } returns EventType.NOTIFICATION })
        )
        coEvery { eventsUseCase.getAllNotificationEvents() } returns result
        notificationListViewModel.getAllNotificationEvents()
        Assert.assertEquals(
            result,
            notificationListViewModel.notificationListResult.value
        )
    }

    @Test
    fun getNotificationListError() {
        val result = Result.Error(mockk())
        coEvery { eventsUseCase.getAllNotificationEvents() } returns result
        notificationListViewModel.getAllNotificationEvents()
        Assert.assertEquals(
            result,
            notificationListViewModel.notificationListResult.value
        )
    }

    @Test
    fun getNotificationListFlow() {
        coEvery { eventsUseCase.getAllNotificationEvents() } returns mockk()
        notificationListViewModel.getAllNotificationEvents()
        coVerify { eventsUseCase.getAllNotificationEvents() }
    }

    @Test
    fun setAllNotificationsAsRead() {
        coEvery { eventsUseCase.setAllNotificationsAsRead() } just Runs
        notificationListViewModel.setAllNotificationsAsRead()
        coVerify { eventsUseCase.setAllNotificationsAsRead() }
    }
}
