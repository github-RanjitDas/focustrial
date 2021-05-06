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
        coEvery { eventsUseCase.getNotificationEvents() } returns result
        notificationListViewModel.getNotificationEvents()
        Assert.assertEquals(
            result,
            notificationListViewModel.notificationEventsResult.value
        )
    }

    @Test
    fun getNotificationListError() {
        val result = Result.Error(mockk())
        coEvery { eventsUseCase.getNotificationEvents() } returns result
        notificationListViewModel.getNotificationEvents()
        Assert.assertEquals(
            result,
            notificationListViewModel.notificationEventsResult.value
        )
    }

    @Test
    fun getNotificationListFlow() {
        coEvery { eventsUseCase.getNotificationEvents() } returns mockk()
        notificationListViewModel.getNotificationEvents()
        coVerify { eventsUseCase.getNotificationEvents() }
    }

    @Test
    fun setAllNotificationsAsRead() {
        coEvery { eventsUseCase.setAllNotificationsAsRead() } just Runs
        notificationListViewModel.setAllNotificationsAsRead()
        coVerify { eventsUseCase.setAllNotificationsAsRead() }
    }

    @Test
    fun getCameraEventsFlow() {
        coEvery { eventsUseCase.getCameraEvents() } returns mockk()
        notificationListViewModel.getCameraEvents()
        coVerify { eventsUseCase.getCameraEvents() }
    }

    @Test
    fun getCameraEventsSuccess() {
        coEvery { eventsUseCase.getCameraEvents() } returns Result.Success(mockk())
        notificationListViewModel.getCameraEvents()
        Assert.assertTrue(
            notificationListViewModel.cameraEventsResult.value is Result.Success
        )
    }

    @Test
    fun getCameraEventsError() {
        coEvery { eventsUseCase.getCameraEvents() } returns Result.Error(mockk())
        notificationListViewModel.getCameraEvents()
        Assert.assertTrue(
            notificationListViewModel.cameraEventsResult.value is Result.Error
        )
    }
}
