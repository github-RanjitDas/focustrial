package com.lawmobile.data.repository.events

import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.lawmobile.data.datasource.local.events.EventsLocalDataSource
import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSource
import com.lawmobile.data.utils.DateHelper
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.EventType
import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
internal class EventsRepositoryImplTest {

    private val eventsRemoteDataSource: EventsRemoteDataSource = mockk()
    private val eventsLocalDataSource: EventsLocalDataSource = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val eventsRepositoryImpl: EventsRepositoryImpl by lazy {
        EventsRepositoryImpl(eventsRemoteDataSource, eventsLocalDataSource)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun getCameraEventsFlowWithEventsInDB() = runBlockingTest {
        val remoteEvents = Result.Success(
            listOf(
                LogEvent(
                    name = "Camera",
                    date = DateHelper.getTodayDateAtStartOfTheDay(),
                    type = "warn:Low battery",
                    value = "Charge your camera",
                    additionalInformation = null
                ),
                LogEvent(
                    name = "Notification",
                    date = DateHelper.getTodayDateAtStartOfTheDay(),
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
        coEvery { eventsLocalDataSource.getEventsCount() } returns 1
        coEvery { eventsLocalDataSource.clearAllEvents() } just Runs
        coEvery { eventsLocalDataSource.saveAllEvents(any()) } returns Result.Success(Unit)

        eventsRepositoryImpl.getCameraEvents()

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
    fun getCameraEventsWithEmptyDB() = runBlockingTest {
        val remoteEvents = Result.Success(
            listOf(
                LogEvent(
                    name = "Camera",
                    date = DateHelper.getTodayDateAtStartOfTheDay(),
                    type = "warn:Low battery",
                    value = "Charge your camera",
                    additionalInformation = null
                ),
                LogEvent(
                    name = "Notification",
                    date = DateHelper.getTodayDateAtStartOfTheDay(),
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
        coEvery { eventsLocalDataSource.getEventsCount() } returns 0
        coEvery { eventsLocalDataSource.saveAllEvents(any()) } returns Result.Success(Unit)

        eventsRepositoryImpl.getCameraEvents()

        coVerify {
            eventsLocalDataSource.deleteOutdatedEvents(any())
            eventsLocalDataSource.getEventsCount()
            eventsLocalDataSource.getAllEvents()
            eventsRemoteDataSource.getCameraEvents()
            eventsLocalDataSource.saveAllEvents(any())
        }
    }

    @Test
    fun getCameraEventsSuccess() = runBlockingTest {
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

        coEvery { eventsLocalDataSource.getEventsCount() } returns 1
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsLocalDataSource.getAllEvents() } returns mockk(relaxed = true)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(notificationList)
        coEvery { eventsLocalDataSource.saveAllEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsLocalDataSource.clearAllEvents() } just Runs

        Assert.assertTrue(eventsRepositoryImpl.getCameraEvents() is Result.Success)
    }

    @Test
    fun getCameraEventsErrorInDB() = runBlockingTest {
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

        coEvery { eventsLocalDataSource.getEventsCount() } returns 1
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsLocalDataSource.getAllEvents() } returns mockk(relaxed = true)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(notificationList)
        coEvery { eventsLocalDataSource.saveAllEvents(any()) } returns Result.Success(mockk())
        coEvery { eventsLocalDataSource.clearAllEvents() } just Runs

        Assert.assertTrue(eventsRepositoryImpl.getCameraEvents() is Result.Success)
    }

    @Test
    fun getCameraEventsEmptyEventsDBError() = runBlockingTest {
        val notificationList = listOf(
            LogEvent(
                name = "Notification",
                date = DateHelper.getTodayDateAtStartOfTheDay(),
                type = "warn:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )

        coEvery { eventsLocalDataSource.getEventsCount() } returns 0
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsLocalDataSource.getAllEvents() } returns mockk(relaxed = true)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(notificationList)
        coEvery { eventsLocalDataSource.saveAllEvents(any()) } returns Result.Error(mockk())

        Assert.assertTrue(eventsRepositoryImpl.getCameraEvents() is Result.Error)
    }

    @Test
    fun getCameraEventsError() = dispatcher.runBlockingTest {
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Error(mockk(relaxed = true))
        Assert.assertTrue(eventsRepositoryImpl.getCameraEvents() is Result.Error)
    }

    @Test
    fun getAllNotificationEvents() = runBlockingTest {
        mockkObject(CameraInfo)
        val cameraEventList = listOf<LocalCameraEvent>(
            mockk(relaxed = true) {
                every { eventType } returns EventType.NOTIFICATION.value
            },
            mockk(relaxed = true) {
                every { eventType } returns EventType.CAMERA.value
            }
        )
        coEvery { eventsLocalDataSource.getNotificationEvents(any()) } returns Result.Success(
            cameraEventList
        )

        val result = eventsRepositoryImpl.getNotificationEvents() as Result.Success
        Assert.assertTrue(result.data.size == 2)
    }

    @Test
    fun isPossibleToReadLogFlow() = runBlockingTest {
        every { eventsRemoteDataSource.isPossibleToReadLog() } returns true
        eventsRepositoryImpl.isPossibleToReadLog()
        verify { eventsRemoteDataSource.isPossibleToReadLog() }
    }

    @Test
    fun isPossibleToReadLogTrue() = runBlockingTest {
        every { eventsRemoteDataSource.isPossibleToReadLog() } returns true
        Assert.assertTrue(eventsRepositoryImpl.isPossibleToReadLog())
    }

    @Test
    fun isPossibleToReadLogFalse() = runBlockingTest {
        every { eventsRemoteDataSource.isPossibleToReadLog() } returns false
        Assert.assertFalse(eventsRepositoryImpl.isPossibleToReadLog())
    }

    @Test
    fun setAllNotificationsAsReadFlow() = runBlockingTest {
        coEvery { eventsLocalDataSource.setAllNotificationsAsRead() } just Runs
        eventsRepositoryImpl.setAllNotificationsAsRead()
        coVerify { eventsLocalDataSource.setAllNotificationsAsRead() }
    }

    @Test
    fun clearAllEventsFlow() = runBlockingTest {
        coEvery { eventsLocalDataSource.clearAllEvents() } just Runs
        eventsRepositoryImpl.clearAllEvents()
        coVerify { eventsLocalDataSource.clearAllEvents() }
    }

    @Test
    fun getPendingNotificationsCountFlow() = runBlockingTest {
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getAllEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getPendingNotificationsCount() } returns Result.Success(1)
        coEvery { eventsLocalDataSource.getEventsCount() } returns 3
        eventsRepositoryImpl.getPendingNotificationsCount()
        coVerify { eventsLocalDataSource.getPendingNotificationsCount() }
    }

    @Test
    fun getPendingNotificationsCountSuccess() = runBlockingTest {
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getAllEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getEventsCount() } returns 3
        coEvery { eventsLocalDataSource.getPendingNotificationsCount() } returns Result.Success(1)
        Assert.assertTrue(eventsRepositoryImpl.getPendingNotificationsCount() is Result.Success)
    }

    @Test
    fun getPendingNotificationsCountError() = runBlockingTest {
        coEvery { eventsLocalDataSource.deleteOutdatedEvents(any()) } returns Result.Success(Unit)
        coEvery { eventsRemoteDataSource.getCameraEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getAllEvents() } returns Result.Success(mockk(relaxed = true))
        coEvery { eventsLocalDataSource.getEventsCount() } returns 3
        coEvery { eventsLocalDataSource.getPendingNotificationsCount() } returns Result.Error(mockk())
        Assert.assertTrue(eventsRepositoryImpl.getPendingNotificationsCount() is Result.Error)
    }

    @Test
    fun saveEventFlow() = runBlockingTest {
        coEvery { eventsLocalDataSource.saveEvent(any()) } just Runs
        eventsRepositoryImpl.saveEvent(mockk(relaxed = true))
        coVerify { eventsLocalDataSource.saveEvent(any()) }
    }
}
