package com.lawmobile.database.dao

import com.lawmobile.data.dao.CameraEventsDao
import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.lawmobile.database.Database
import com.lawmobile.database.mappers.DbEventsMapper

class CameraEventsDaoImpl(private val database: Database) : CameraEventsDao {
    override fun getAllEvents(): List<LocalCameraEvent> {
        val dbEvents = database.databaseQueries.getAllEvents().executeAsList()
        return DbEventsMapper.dbToLocalList(dbEvents)
    }

    override fun getNotificationEvents(date: String): List<LocalCameraEvent> {
        val dbEvents = database.databaseQueries.getNotificationEvents(date).executeAsList()
        return DbEventsMapper.dbToLocalList(dbEvents)
    }

    override fun getEventsCount() = database.databaseQueries.getEventsCount().executeAsOne().toInt()

    override fun getPendingNotificationsCount() =
        database.databaseQueries.getPendingNotificationsCount().executeAsOne().toInt()

    override fun saveEvent(event: LocalCameraEvent) {
        with(event) {
            database.databaseQueries.saveEvent(
                name = name,
                eventType = eventType,
                eventTag = eventTag,
                value = value,
                date = date,
                isRead = isRead
            )
        }
    }

    override fun setAllNotificationsAsRead() {
        database.databaseQueries.setAllNotificationsAsRead()
    }

    override fun deleteOutdatedEvents(date: String) {
        database.databaseQueries.deleteOutdatedEvents(date)
    }

    override fun clearAllEvents() {
        database.databaseQueries.clearAllEvents()
    }
}
