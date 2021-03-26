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

    override fun getEventById(id: Long): LocalCameraEvent {
        val dbEvent = database.databaseQueries.getEventById(id).executeAsOne()
        return DbEventsMapper.dbToLocal(dbEvent)
    }

    override fun getLastEventId(): Long {
        return database.databaseQueries.getLastEventId().executeAsOne()
    }

    override fun saveEvent(event: LocalCameraEvent) {
        with(event) {
            database.databaseQueries.saveEvent(
                id = null,
                _name = name,
                _eventType = eventType,
                _eventTag = eventTag,
                _value = value,
                _date = date,
                _isRead = isRead
            )
        }
    }

    override fun setEventRead(isRead: Long, date: String) {
        database.databaseQueries.setEventRead(isRead, date)
    }

    override fun clearAllEvents() {
        database.databaseQueries.clearAllEvents()
    }
}
