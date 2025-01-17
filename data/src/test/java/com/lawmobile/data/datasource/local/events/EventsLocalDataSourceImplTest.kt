package com.lawmobile.data.datasource.local.events

import com.lawmobile.data.dao.CameraEventsDao
import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class EventsLocalDataSourceImplTest {

    private val cameraEventsDao: CameraEventsDao = mockk()

    private val eventsLocalDataSourceImpl by lazy {
        EventsLocalDataSourceImpl(cameraEventsDao)
    }

    @Test
    fun getAllEventsFlow() {
        every { cameraEventsDao.getAllEvents() } returns mockk()
        runBlocking { eventsLocalDataSourceImpl.getAllEvents() }
        verify { cameraEventsDao.getAllEvents() }
    }

    @Test
    fun getAllEventsSuccess() {
        every { cameraEventsDao.getAllEvents() } returns mockk()
        runBlocking {
            Assert.assertTrue(eventsLocalDataSourceImpl.getAllEvents() is Result.Success)
        }
    }

    @Test
    fun getAllEventsError() {
        every { cameraEventsDao.getAllEvents() } throws Exception()
        runBlocking {
            Assert.assertTrue(eventsLocalDataSourceImpl.getAllEvents() is Result.Error)
        }
    }

    @Test
    fun getAllEventsResult() {
        val events = mockk<List<LocalCameraEvent>>()
        every { cameraEventsDao.getAllEvents() } returns events
        runBlocking {
            val result = eventsLocalDataSourceImpl.getAllEvents() as Result.Success
            Assert.assertEquals(events, result.data)
        }
    }

    @Test
    fun saveAllEventsFlow() {
        every { cameraEventsDao.saveEvent(any()) } just Runs
        runBlocking { eventsLocalDataSourceImpl.saveAllEvents(listOf(mockk())) }
        verify { cameraEventsDao.saveEvent(any()) }
    }

    @Test
    fun saveAllEventsSuccess() {
        every { cameraEventsDao.saveEvent(any()) } just Runs
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.saveAllEvents(listOf(mockk())) is Result.Success
            )
        }
    }

    @Test
    fun saveAllEventsError() {
        every { cameraEventsDao.saveEvent(any()) } throws Exception()
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.saveAllEvents(listOf(mockk())) is Result.Error
            )
        }
    }

    @Test
    fun clearAllEventsFlow() {
        every { cameraEventsDao.clearAllEvents() } just Runs
        runBlocking { eventsLocalDataSourceImpl.clearAllEvents() }
        verify { cameraEventsDao.clearAllEvents() }
    }

    @Test
    fun clearAllEventsError() {
        every { cameraEventsDao.clearAllEvents() } throws Exception()
        runBlocking { eventsLocalDataSourceImpl.clearAllEvents() }
        verify { cameraEventsDao.clearAllEvents() }
    }

    @Test
    fun getAllNotificationEventsFlow() {
        every { cameraEventsDao.getNotificationEvents(any()) } returns mockk()
        runBlocking { eventsLocalDataSourceImpl.getNotificationEvents("") }
        verify { cameraEventsDao.getNotificationEvents(any()) }
    }

    @Test
    fun getAllNotificationEventsSuccess() {
        every { cameraEventsDao.getNotificationEvents(any()) } returns mockk()
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.getNotificationEvents("") is Result.Success
            )
        }
    }

    @Test
    fun getAllNotificationEventsError() {
        every { cameraEventsDao.getNotificationEvents(any()) } throws Exception()
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.getNotificationEvents("") is Result.Error
            )
        }
    }

    @Test
    fun getPendingNotificationsCountFlow() {
        every { cameraEventsDao.getPendingNotificationsCount() } returns 1
        runBlocking { eventsLocalDataSourceImpl.getPendingNotificationsCount() }
        verify { cameraEventsDao.getPendingNotificationsCount() }
    }

    @Test
    fun getPendingNotificationsCountSuccess() {
        every { cameraEventsDao.getPendingNotificationsCount() } returns 1
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.getPendingNotificationsCount() is Result.Success
            )
        }
    }

    @Test
    fun getPendingNotificationsCountError() {
        every { cameraEventsDao.getPendingNotificationsCount() } throws Exception()
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.getPendingNotificationsCount() is Result.Error
            )
        }
    }

    @Test
    fun setAllNotificationsAsReadFlow() {
        every { cameraEventsDao.setAllNotificationsAsRead() } returns mockk()
        runBlocking { eventsLocalDataSourceImpl.setAllNotificationsAsRead() }
        verify { cameraEventsDao.setAllNotificationsAsRead() }
    }

    @Test
    fun setAllNotificationsAsReadError() {
        every { cameraEventsDao.setAllNotificationsAsRead() } throws Exception()
        runBlocking { eventsLocalDataSourceImpl.setAllNotificationsAsRead() }
        verify { cameraEventsDao.setAllNotificationsAsRead() }
    }

    @Test
    fun getEventsCountFlow() {
        every { cameraEventsDao.getEventsCount() } returns 1
        runBlocking { eventsLocalDataSourceImpl.getEventsCount() }
        verify { cameraEventsDao.getEventsCount() }
    }

    @Test
    fun getEventsCountSuccess() {
        every { cameraEventsDao.getEventsCount() } returns 1
        runBlocking { Assert.assertEquals(1, eventsLocalDataSourceImpl.getEventsCount()) }
    }

    @Test
    fun getEventsCountError() {
        every { cameraEventsDao.getEventsCount() } throws Exception()
        runBlocking { Assert.assertEquals(0, eventsLocalDataSourceImpl.getEventsCount()) }
    }

    @Test
    fun saveEventFlow() {
        every { cameraEventsDao.saveEvent(any()) } just Runs
        runBlocking { eventsLocalDataSourceImpl.saveEvent(mockk(relaxed = true)) }
        verify { cameraEventsDao.saveEvent(any()) }
    }

    @Test
    fun saveEventError() {
        every { cameraEventsDao.saveEvent(any()) } throws Exception()
        runBlocking { eventsLocalDataSourceImpl.saveEvent(mockk(relaxed = true)) }
        verify { cameraEventsDao.saveEvent(any()) }
    }

    @Test
    fun deleteOutdatedEventsFlow() {
        every { cameraEventsDao.deleteOutdatedEvents(any()) } just Runs
        runBlocking { eventsLocalDataSourceImpl.deleteOutdatedEvents("") }
        verify { cameraEventsDao.deleteOutdatedEvents(any()) }
    }

    @Test
    fun deleteOutdatedEventsError() {
        every { cameraEventsDao.deleteOutdatedEvents(any()) } throws Exception()
        runBlocking { eventsLocalDataSourceImpl.deleteOutdatedEvents("") }
        verify { cameraEventsDao.deleteOutdatedEvents(any()) }
    }
}
