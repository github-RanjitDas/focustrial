package com.lawmobile.data.repository.events

import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.lawmobile.data.datasource.local.events.EventsLocalDataSource
import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSource
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.EventType
import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class EventsRepositoryImplTest {

    private val eventsRemoteDataSource: EventsRemoteDataSource = mockk()
    private val eventsLocalDataSource: EventsLocalDataSource = mockk()

    private val eventsRepositoryImpl: EventsRepositoryImpl by lazy {
        EventsRepositoryImpl(eventsRemoteDataSource, eventsLocalDataSource)
    }

    @Test
    fun getCameraEventsFlowWithEventsInDB() {
        val remoteEvents = Result.Success(
            listOf(
                LogEvent(
                    name = "Camera",
                    date = "20/12/2020",
                    type = "warn:Low battery",
                    value = "Charge your camera",
                    additionalInformation = null
                ),
                LogEvent(
                    name = "Notification",
                    date = "20/11/2020",
                    type = "warn:Low battery",
                    value = "Charge your camera",
                    additionalInformation = null
                )
            )
        )

        val localEvents = Result.Success(
            listOf(
                LocalCameraEvent(
                    name = "Camera",
                    date = "20/12/2020",
                    eventType = "Camera",
                    eventTag = "Warning",
                    value = "Charge your camera",
                    isRead = 0
                )
            )
        )

        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns remoteEvents
        coEvery { eventsLocalDataSource.getAllEvents() } returns localEvents
        every { eventsLocalDataSource.getEventsCount() } returns 1
        coEvery { eventsLocalDataSource.clearAllEvents() } just Runs
        coEvery { eventsLocalDataSource.saveAllEvents(any()) } returns Result.Success(Unit)

        runBlocking { eventsRepositoryImpl.getCameraEvents() }

        coVerify {
            eventsLocalDataSource.deleteOutdatedEvents(any())
            eventsLocalDataSource.getEventsCount()
            eventsLocalDataSource.getAllEvents()
            eventsRemoteDataSource.getCameraEvents()
            eventsLocalDataSource.clearAllEvents()
            eventsLocalDataSource.saveAllEvents(any())
        }
    }

    @Test
    fun getCameraEventsWithEmptyDB() {
        val remoteEvents = Result.Success(
            listOf(
                LogEvent(
                    name = "Camera",
                    date = "20/12/2020",
                    type = "warn:Low battery",
                    value = "Charge your camera",
                    additionalInformation = null
                ),
                LogEvent(
                    name = "Notification",
                    date = "20/11/2020",
                    type = "warn:Low battery",
                    value = "Charge your camera",
                    additionalInformation = null
                )
            )
        )

        val localEvents = Result.Success(
            emptyList<LocalCameraEvent>()
        )

        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns remoteEvents
        coEvery { eventsLocalDataSource.getAllEvents() } returns localEvents
        every { eventsLocalDataSource.getEventsCount() } returns 0
        coEvery { eventsLocalDataSource.saveAllEvents(any()) } returns Result.Success(Unit)

        runBlocking { eventsRepositoryImpl.getCameraEvents() }

        coVerify {
            eventsLocalDataSource.deleteOutdatedEvents(any())
            eventsLocalDataSource.getEventsCount()
            eventsLocalDataSource.getAllEvents()
            eventsRemoteDataSource.getCameraEvents()
            eventsLocalDataSource.saveAllEvents(any())
        }
    }

    @Test
    fun getCameraEventsSuccess() {
        val notificationList = listOf(
            LogEvent(
                name = "Notification",
                date = "20/12/2020",
                type = "warn:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            ),
            LogEvent(
                name = "Notification",
                date = "20/11/2020",
                type = "warn:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )

        every { eventsLocalDataSource.getEventsCount() } returns 1
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsLocalDataSource.getAllEvents() } returns mockk(relaxed = true)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(notificationList)
        coEvery { eventsLocalDataSource.saveAllEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsLocalDataSource.clearAllEvents() } just Runs

        runBlocking {
            Assert.assertTrue(
                eventsRepositoryImpl.getCameraEvents() is Result.Success
            )
        }
    }

    @Test
    fun getCameraEventsErrorInDB() {
        val notificationList = listOf(
            LogEvent(
                name = "Notification",
                date = "20/12/2020",
                type = "warn:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            ),
            LogEvent(
                name = "Notification",
                date = "20/11/2020",
                type = "warn:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )

        every { eventsLocalDataSource.getEventsCount() } returns 1
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsLocalDataSource.getAllEvents() } returns mockk(relaxed = true)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(notificationList)
        coEvery { eventsLocalDataSource.saveAllEvents(any()) } returns Result.Success(mockk())
        coEvery { eventsLocalDataSource.clearAllEvents() } just Runs

        runBlocking {
            Assert.assertTrue(
                eventsRepositoryImpl.getCameraEvents() is Result.Success
            )
        }
    }

    @Test
    fun getCameraEventsEmptyEventsDBError() {
        val notificationList = listOf(
            LogEvent(
                name = "Notification",
                date = "20/12/2020",
                type = "warn:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )

        every { eventsLocalDataSource.getEventsCount() } returns 0
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsLocalDataSource.getAllEvents() } returns mockk(relaxed = true)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(notificationList)
        coEvery { eventsLocalDataSource.saveAllEvents(any()) } returns Result.Error(mockk())

        runBlocking {
            Assert.assertTrue(
                eventsRepositoryImpl.getCameraEvents() is Result.Error
            )
        }
    }

    @Test
    fun getCameraEventsError() {
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Error(mockk(relaxed = true))
        runBlocking {
            Assert.assertTrue(
                eventsRepositoryImpl.getCameraEvents() is Result.Error
            )
        }
    }

    @Test
    fun getAllNotificationEvents() {
        mockkObject(CameraInfo)
        val cameraEventList = listOf<LocalCameraEvent>(
            mockk(relaxed = true) {
                every { eventType } returns EventType.NOTIFICATION.value
            },
            mockk(relaxed = true) {
                every { eventType } returns EventType.CAMERA.value
            }
        )
        coEvery { eventsLocalDataSource.getNotificationEvents() } returns Result.Success(
            cameraEventList
        )
        runBlocking {
            val result = eventsRepositoryImpl.getNotificationEvents() as Result.Success
            Assert.assertTrue(result.data.size == 2)
        }
    }

    @Test
    fun isPossibleToReadLogFlow() {
        every { eventsRemoteDataSource.isPossibleToReadLog() } returns true
        eventsRepositoryImpl.isPossibleToReadLog()
        verify { eventsRemoteDataSource.isPossibleToReadLog() }
    }

    @Test
    fun isPossibleToReadLogTrue() {
        every { eventsRemoteDataSource.isPossibleToReadLog() } returns true
        Assert.assertTrue(eventsRepositoryImpl.isPossibleToReadLog())
    }

    @Test
    fun isPossibleToReadLogFalse() {
        every { eventsRemoteDataSource.isPossibleToReadLog() } returns false
        Assert.assertFalse(eventsRepositoryImpl.isPossibleToReadLog())
    }

    @Test
    fun setAllNotificationsAsReadFlow() {
        coEvery { eventsLocalDataSource.setAllNotificationsAsRead() } just Runs
        runBlocking {
            eventsRepositoryImpl.setAllNotificationsAsRead()
        }
        coVerify { eventsLocalDataSource.setAllNotificationsAsRead() }
    }

    @Test
    fun clearAllEventsFlow() {
        coEvery { eventsLocalDataSource.clearAllEvents() } just Runs
        runBlocking { eventsRepositoryImpl.clearAllEvents() }
        coVerify { eventsLocalDataSource.clearAllEvents() }
    }

    @Test
    fun getPendingNotificationsCountFlow() {
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getAllEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getPendingNotificationsCount() } returns Result.Success(1)
        coEvery { eventsLocalDataSource.getEventsCount() } returns 3
        runBlocking { eventsRepositoryImpl.getPendingNotificationsCount() }
        coVerify { eventsLocalDataSource.getPendingNotificationsCount() }
    }

    @Test
    fun getPendingNotificationsCountSuccess() {
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getAllEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getEventsCount() } returns 3
        coEvery { eventsLocalDataSource.getPendingNotificationsCount() } returns Result.Success(1)
        runBlocking {
            Assert.assertTrue(
                eventsRepositoryImpl.getPendingNotificationsCount() is Result.Success
            )
        }
    }

    @Test
    fun getPendingNotificationsCountError() {
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getAllEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getEventsCount() } returns 3
        coEvery { eventsLocalDataSource.getPendingNotificationsCount() } returns Result.Error(mockk())
        runBlocking {
            Assert.assertTrue(
                eventsRepositoryImpl.getPendingNotificationsCount() is Result.Error
            )
        }
    }

    @Test
    fun saveEventFlow() {
        coEvery { eventsLocalDataSource.saveEvent(any()) } just Runs
        runBlocking { eventsRepositoryImpl.saveEvent(mockk(relaxed = true)) }
        coVerify { eventsLocalDataSource.saveEvent(any()) }
    }
}
