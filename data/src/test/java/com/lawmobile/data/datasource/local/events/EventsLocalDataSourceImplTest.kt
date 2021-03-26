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
            Assert.assertTrue(
                eventsLocalDataSourceImpl.getAllEvents() is Result.Success
            )
        }
    }

    @Test
    fun getAllEventsError() {
        every { cameraEventsDao.getAllEvents() } throws Exception()
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.getAllEvents() is Result.Error
            )
        }
    }

    @Test
    fun getAllEventsResult() {
        val events = mockk<List<LocalCameraEvent>>()
        every { cameraEventsDao.getAllEvents() } returns events
        runBlocking {
            val result = eventsLocalDataSourceImpl.getAllEvents() as Result.Success
            Assert.assertEquals(
                events,
                result.data
            )
        }
    }

    @Test
    fun getLastEventFlow() {
        every { cameraEventsDao.getLastEventId() } returns 1
        every { cameraEventsDao.getEventById(any()) } returns mockk()
        runBlocking { eventsLocalDataSourceImpl.getLastEvent() }
        verify {
            cameraEventsDao.getLastEventId()
            cameraEventsDao.getEventById(any())
        }
    }

    @Test
    fun getLastEventSuccess() {
        every { cameraEventsDao.getLastEventId() } returns 1
        every { cameraEventsDao.getEventById(any()) } returns mockk()
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.getLastEvent() is Result.Success
            )
        }
    }

    @Test
    fun getLastEventError() {
        every { cameraEventsDao.getLastEventId() } returns 1
        every { cameraEventsDao.getEventById(any()) } throws Exception()
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.getLastEvent() is Result.Error
            )
        }
    }

    @Test
    fun getLastEventResult() {
        val event: LocalCameraEvent = mockk()
        every { cameraEventsDao.getLastEventId() } returns 1
        every { cameraEventsDao.getEventById(any()) } returns event
        runBlocking {
            val result = eventsLocalDataSourceImpl.getLastEvent() as Result.Success
            Assert.assertEquals(
                event,
                result.data
            )
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
    fun setEventReadFlow() {
        every { cameraEventsDao.setEventRead(any(), any()) } just Runs
        runBlocking { eventsLocalDataSourceImpl.setEventRead(true, "") }
        verify { cameraEventsDao.setEventRead(any(), any()) }
    }

    @Test
    fun setEventReadSuccess() {
        every { cameraEventsDao.setEventRead(any(), any()) } just Runs
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.setEventRead(true, "") is Result.Success
            )
        }
    }

    @Test
    fun setEventReadError() {
        every { cameraEventsDao.setEventRead(any(), any()) } throws Exception()
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.setEventRead(true, "") is Result.Error
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
    fun clearAllEventsSuccess() {
        every { cameraEventsDao.clearAllEvents() } just Runs
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.clearAllEvents() is Result.Success
            )
        }
    }

    @Test
    fun clearAllEventsError() {
        every { cameraEventsDao.clearAllEvents() } throws Exception()
        runBlocking {
            Assert.assertTrue(
                eventsLocalDataSourceImpl.clearAllEvents() is Result.Error
            )
        }
    }
}
