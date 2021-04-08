package com.lawmobile.data.datasource.local.events

import com.lawmobile.data.dao.CameraEventsDao
import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result

class EventsLocalDataSourceImpl(private val cameraEventsDao: CameraEventsDao) :
    EventsLocalDataSource {

    override suspend fun getAllEvents(): Result<List<LocalCameraEvent>> =
        try {
            Result.Success(cameraEventsDao.getAllEvents())
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun getNotificationEvents(): Result<List<LocalCameraEvent>> =
        try {
            Result.Success(cameraEventsDao.getNotificationEvents())
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun getLastEvent(): Result<LocalCameraEvent> =
        try {
            val eventId = cameraEventsDao.getLastEventId()
            Result.Success(cameraEventsDao.getEventById(eventId))
        } catch (e: Exception) {
            Result.Error(e)
        }

    override fun getEventsCount() =
        try {
            cameraEventsDao.getEventsCount()
        } catch (e: Exception) {
            println("error getting the events count" + e.message)
            0
        }

    override suspend fun getPendingNotificationsCount(): Result<Int> =
        try {
            Result.Success(cameraEventsDao.getPendingNotificationsCount())
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun saveEvent(localEvent: LocalCameraEvent) {
        try {
            cameraEventsDao.saveEvent(localEvent)
        } catch (e: Exception) {
            println("error saving event in the database" + e.message)
        }
    }

    override suspend fun saveAllEvents(events: List<LocalCameraEvent>): Result<Unit> =
        try {
            events.forEach {
                cameraEventsDao.saveEvent(it)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun setEventRead(isRead: Boolean, date: String): Result<Unit> =
        try {
            val isReadLong: Long = if (isRead) 1 else 0
            Result.Success(cameraEventsDao.setEventRead(isReadLong, date))
        } catch (e: Exception) {
            Result.Error(e)
        }

    override suspend fun setAllNotificationsAsRead() =
        try {
            cameraEventsDao.setAllNotificationsAsRead()
        } catch (e: Exception) {
            println("error updating notifications in database" + e.message)
        }

    override suspend fun clearAllEvents() =
        try {
            cameraEventsDao.clearAllEvents()
        } catch (e: Exception) {
            println("error deleting events in database" + e.message)
        }
}
