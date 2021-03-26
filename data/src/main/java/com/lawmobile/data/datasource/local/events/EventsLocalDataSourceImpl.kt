package com.lawmobile.data.datasource.local.events

import com.lawmobile.data.dao.CameraEventsDao
import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result

class EventsLocalDataSourceImpl(private val cameraEventsDao: CameraEventsDao) : EventsLocalDataSource {
    override suspend fun getAllEvents(): Result<List<LocalCameraEvent>> =
        try {
            Result.Success(cameraEventsDao.getAllEvents())
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

    override suspend fun clearAllEvents() =
        try {
            Result.Success(cameraEventsDao.clearAllEvents())
        } catch (e: Exception) {
            Result.Error(e)
        }
}
