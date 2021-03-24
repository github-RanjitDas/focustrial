package com.lawmobile.data.repository.events

import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSource
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.EventType
import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class EventsRepositoryImplTest {

    private val eventsRemoteDataSource: EventsRemoteDataSource = mockk()

    private val eventsRepositoryImpl: EventsRepositoryImpl by lazy {
        EventsRepositoryImpl(eventsRemoteDataSource)
    }

    @Test
    fun getLogEventsFlow() {
        coEvery { eventsRemoteDataSource.getLogEvents() } returns Result.Success(mockk(relaxed = true))
        runBlocking { eventsRepositoryImpl.getLogEvents() }
        coVerify { eventsRemoteDataSource.getLogEvents() }
    }

    @Test
    fun getLogEventsSuccess() {
        mockkObject(CameraInfo)
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
                date = "20/12/2020",
                type = "warn:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )
        CameraInfo.cameraEventList = listOf(mockk(relaxed = true))
        coEvery { eventsRemoteDataSource.getLogEvents() } returns Result.Success(
            notificationList
        )
        runBlocking {
            Assert.assertTrue(
                eventsRepositoryImpl.getLogEvents() is Result.Success
            )
        }
    }

    @Test
    fun getLogEventsError() {
        coEvery { eventsRemoteDataSource.getLogEvents() } returns Result.Error(mockk(relaxed = true))
        runBlocking {
            Assert.assertTrue(
                eventsRepositoryImpl.getLogEvents() is Result.Error
            )
        }
    }

    @Test
    fun getNotificationList() {
        mockkObject(CameraInfo)
        CameraInfo.cameraEventList = listOf(
            mockk {
                every { eventType } returns EventType.NOTIFICATION
            },
            mockk {
                every { eventType } returns EventType.CAMERA
            }
        )
        Assert.assertTrue(eventsRepositoryImpl.getNotificationList().size == 1)
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
}
