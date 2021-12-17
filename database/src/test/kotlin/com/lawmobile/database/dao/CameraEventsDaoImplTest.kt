package com.lawmobile.database.dao

import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.lawmobile.database.Database
import com.lawmobile.database.DbCameraEvent
import com.lawmobile.database.mappers.DbEventsMapper.toLocalList
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class CameraEventsDaoImplTest {

    private val database: Database = mockk()

    private val cameraEventsDaoImpl by lazy {
        CameraEventsDaoImpl(database)
    }

    @Test
    fun getAllEventsFlow() {
        every {
            database.databaseQueries.getAllEvents().executeAsList()
        } returns mockk(relaxed = true)
        cameraEventsDaoImpl.getAllEvents()
        verify { database.databaseQueries.getAllEvents().executeAsList() }
    }

    @Test
    fun getAllEventsResult() {
        val result = listOf(
            DbCameraEvent(
                1,
                "event",
                "Camera",
                "Information",
                "Greetings",
                "20/12/20",
                1
            ),
            DbCameraEvent(
                2,
                "event",
                "Camera",
                "Information",
                "Greetings",
                "20/12/20",
                0
            )
        )
        every { database.databaseQueries.getAllEvents().executeAsList() } returns result
        Assert.assertEquals(
            result.toLocalList(),
            cameraEventsDaoImpl.getAllEvents()
        )
    }

    @Test
    fun saveEventFlow() {
        val event = LocalCameraEvent(
            1,
            "event",
            "Camera",
            "Information",
            "Greetings",
            "20/12/20",
            1
        )
        every {
            database.databaseQueries.saveEvent(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } just Runs
        cameraEventsDaoImpl.saveEvent(event)
        verify {
            database.databaseQueries.saveEvent(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun clearAllEventsFlow() {
        every { database.databaseQueries.clearAllEvents() } just Runs
        cameraEventsDaoImpl.clearAllEvents()
        verify { database.databaseQueries.clearAllEvents() }
    }

    @Test
    fun getAllNotificationEventsFlow() {
        val result = mockk<List<DbCameraEvent>>(relaxed = true)
        every { database.databaseQueries.getNotificationEvents(any()).executeAsList() } returns result
        cameraEventsDaoImpl.getNotificationEvents("")
        verify { database.databaseQueries.getNotificationEvents(any()).executeAsList() }
    }

    @Test
    fun getAllNotificationEventsResult() {
        val response = listOf(mockk<DbCameraEvent>(relaxed = true))
        val result = response.toLocalList()
        every { database.databaseQueries.getNotificationEvents(any()).executeAsList() } returns response
        Assert.assertEquals(
            result,
            cameraEventsDaoImpl.getNotificationEvents("")
        )
    }

    @Test
    fun getEventsCountFlow() {
        every { database.databaseQueries.getEventsCount().executeAsOne() } returns 1
        cameraEventsDaoImpl.getEventsCount()
        verify { database.databaseQueries.getEventsCount().executeAsOne() }
    }

    @Test
    fun getEventsCountResult() {
        every { database.databaseQueries.getEventsCount().executeAsOne() } returns 1
        Assert.assertEquals(
            1,
            cameraEventsDaoImpl.getEventsCount()
        )
    }

    @Test
    fun getPendingNotificationsCountFlow() {
        every { database.databaseQueries.getPendingNotificationsCount().executeAsOne() } returns 1
        cameraEventsDaoImpl.getPendingNotificationsCount()
        verify { database.databaseQueries.getPendingNotificationsCount().executeAsOne() }
    }

    @Test
    fun getPendingNotificationsCountResult() {
        every { database.databaseQueries.getPendingNotificationsCount().executeAsOne() } returns 1
        Assert.assertEquals(
            1,
            cameraEventsDaoImpl.getPendingNotificationsCount()
        )
    }

    @Test
    fun setAllNotificationsAsReadFlow() {
        every { database.databaseQueries.setAllNotificationsAsRead() } just Runs
        cameraEventsDaoImpl.setAllNotificationsAsRead()
        verify { database.databaseQueries.setAllNotificationsAsRead() }
    }

    @Test
    fun deleteOutdatedEventsFlow() {
        every { database.databaseQueries.deleteOutdatedEvents(any()) } just Runs
        cameraEventsDaoImpl.deleteOutdatedEvents("")
        verify { database.databaseQueries.deleteOutdatedEvents(any()) }
    }
}
