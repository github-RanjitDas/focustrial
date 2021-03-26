package com.lawmobile.database.mappers

import com.lawmobile.database.DbCameraEvent
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class DbEventsMapperTest {

    @Test
    fun dbToLocalList() {
        val dbEvents = listOf(
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
        val localEvents = DbEventsMapper.dbToLocalList(dbEvents)
        localEvents.forEachIndexed { index, it ->
            with(dbEvents[index]) {
                Assert.assertEquals(date, it.date)
                Assert.assertEquals(eventTag, it.eventTag)
                Assert.assertEquals(eventType, it.eventType)
                Assert.assertEquals(id, it.id)
                Assert.assertEquals(isRead, it.isRead)
                Assert.assertEquals(name, it.name)
                Assert.assertEquals(value, it.value)
            }
        }
    }

    @Test
    fun dbToLocal() {
        val dbEvent = DbCameraEvent(
            1,
            "event",
            "Camera",
            "Information",
            "Greetings",
            "20/12/20",
            1
        )
        val localEvent = DbEventsMapper.dbToLocal(dbEvent)
        dbEvent.let {
            with(localEvent) {
                Assert.assertEquals(date, it.date)
                Assert.assertEquals(eventTag, it.eventTag)
                Assert.assertEquals(eventType, it.eventType)
                Assert.assertEquals(id, it.id)
                Assert.assertEquals(isRead, it.isRead)
                Assert.assertEquals(name, it.name)
                Assert.assertEquals(value, it.value)
            }
        }
    }
}
