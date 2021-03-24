package com.lawmobile.domain.usecase.events

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.repository.events.EventsRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
    fun getLogEventsFlow() {
        coEvery { eventsRepository.getLogEvents() } returns mockk()
        runBlocking { eventsUseCaseImpl.getLogEvents() }
        coVerify { eventsRepository.getLogEvents() }
    }

    @Test
    fun getLogEventsSuccess() {
        coEvery { eventsRepository.getLogEvents() } returns Result.Success(mockk())
        runBlocking {
            Assert.assertTrue(eventsUseCaseImpl.getLogEvents() is Result.Success)
        }
    }

    @Test
    fun getLogEventsError() {
        coEvery { eventsRepository.getLogEvents() } returns Result.Error(mockk())
        runBlocking {
            Assert.assertTrue(eventsUseCaseImpl.getLogEvents() is Result.Error)
        }
    }

    @Test
    fun getNotificationListFlow() {
        every { eventsRepository.getNotificationList() } returns mockk()
        eventsUseCaseImpl.getNotificationList()
        verify { eventsRepository.getNotificationList() }
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
        every { eventsRepository.getNotificationList() } returns notificationList
        Assert.assertEquals(
            notificationList,
            eventsUseCaseImpl.getNotificationList()
        )
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
}
