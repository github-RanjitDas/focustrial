package com.lawmobile.presentation.ui.notificationList

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.usecase.events.EventsUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
    fun getNotificationListResult() {
        val notificationList = listOf<CameraEvent>(
            mockk {
                every { eventType } returns EventType.NOTIFICATION
            },
            mockk {
                every { eventType } returns EventType.NOTIFICATION
            }
        )
        every { eventsUseCase.getNotificationList() } returns notificationList
        Assert.assertEquals(
            notificationList,
            notificationListViewModel.getNotificationList()
        )
    }

    @Test
    fun getNotificationListFlow() {
        every { eventsUseCase.getNotificationList() } returns mockk()
        notificationListViewModel.getNotificationList()
        verify { eventsUseCase.getNotificationList() }
    }
}
