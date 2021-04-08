package com.lawmobile.domain.usecase.events

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.repository.events.EventsRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class EventsUseCaseImplTest {

    private val eventsRepository: EventsRepository = mockk()

    private val eventsUseCaseImpl: EventsUseCaseImpl by lazy {
        EventsUseCaseImpl(eventsRepository)
    }

    @Test
    fun getCameraEventsFlow() {
        coEvery { eventsRepository.getCameraEvents() } returns mockk()
        runBlocking { eventsUseCaseImpl.getCameraEvents() }
        coVerify { eventsRepository.getCameraEvents() }
    }

    @Test
    fun getCameraEventsSuccess() {
        coEvery { eventsRepository.getCameraEvents() } returns Result.Success(mockk())
        runBlocking {
            Assert.assertTrue(eventsUseCaseImpl.getCameraEvents() is Result.Success)
        }
    }

    @Test
    fun getCameraEventsError() {
        coEvery { eventsRepository.getCameraEvents() } returns Result.Error(mockk())
        runBlocking {
            Assert.assertTrue(eventsUseCaseImpl.getCameraEvents() is Result.Error)
        }
    }

    @Test
    fun getAllNotificationEventsFlow() {
        coEvery { eventsRepository.getNotificationEvents() } returns mockk()
        runBlocking { eventsUseCaseImpl.getNotificationEvents() }
        coVerify { eventsRepository.getNotificationEvents() }
    }

    @Test
    fun getAllNotificationEventsResult() {
        val notificationList = listOf<CameraEvent>(
            mockk {
                every { eventType } returns EventType.NOTIFICATION
            },
            mockk {
                every { eventType } returns EventType.NOTIFICATION
            }
        )
        coEvery { eventsRepository.getNotificationEvents() } returns Result.Success(notificationList)
        runBlocking {
            val result = eventsUseCaseImpl.getNotificationEvents() as Result.Success
            Assert.assertEquals(
                notificationList,
                result.data
            )
        }
    }

    @Test
    fun isPossibleToReadLogFlow() {
        every { eventsRepository.isPossibleToReadLog() } returns true
        eventsUseCaseImpl.isPossibleToReadLog()
        verify { eventsRepository.isPossibleToReadLog() }
    }

    @Test
    fun isPossibleToReadLogTrue() {
        every { eventsRepository.isPossibleToReadLog() } returns true
        Assert.assertTrue(eventsUseCaseImpl.isPossibleToReadLog())
    }

    @Test
    fun isPossibleToReadLogFalse() {
        every { eventsRepository.isPossibleToReadLog() } returns false
        Assert.assertFalse(eventsUseCaseImpl.isPossibleToReadLog())
    }

    @Test
    fun setAllNotificationsAsReadFlow() {
        coEvery { eventsRepository.setAllNotificationsAsRead() } just Runs
        runBlocking { eventsUseCaseImpl.setAllNotificationsAsRead() }
        coVerify { eventsRepository.setAllNotificationsAsRead() }
    }

    @Test
    fun clearAllEventsFlow() {
        coEvery { eventsRepository.clearAllEvents() } just Runs
        runBlocking { eventsUseCaseImpl.clearAllEvents() }
        coVerify { eventsRepository.clearAllEvents() }
    }

    @Test
    fun getPendingNotificationsCountFlow() {
        coEvery { eventsRepository.getPendingNotificationsCount() } returns Result.Success(1)
        runBlocking { eventsUseCaseImpl.getPendingNotificationsCount() }
        coVerify { eventsRepository.getPendingNotificationsCount() }
    }

    @Test
    fun getPendingNotificationsCountSuccess() {
        coEvery { eventsRepository.getPendingNotificationsCount() } returns Result.Success(1)
        runBlocking {
            Assert.assertTrue(
                eventsUseCaseImpl.getPendingNotificationsCount() is Result.Success
            )
        }
    }

    @Test
    fun getPendingNotificationsCountError() {
        coEvery { eventsRepository.getPendingNotificationsCount() } returns Result.Error(mockk())
        runBlocking {
            Assert.assertTrue(
                eventsUseCaseImpl.getPendingNotificationsCount() is Result.Error
            )
        }
    }

    @Test
    fun saveEventFlow() {
        coEvery { eventsRepository.saveEvent(any()) } just Runs
        runBlocking { eventsUseCaseImpl.saveEvent(mockk()) }
        coVerify { eventsRepository.saveEvent(any()) }
    }
}
