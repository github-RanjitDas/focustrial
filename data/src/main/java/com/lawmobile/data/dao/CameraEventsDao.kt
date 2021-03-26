package com.lawmobile.data.dao

import com.lawmobile.data.dao.entities.LocalCameraEvent

interface CameraEventsDao {
    fun getAllEvents(): List<LocalCameraEvent>
    fun getEventById(id: Long): LocalCameraEvent
    fun getLastEventId(): Long
    fun saveEvent(event: LocalCameraEvent)
    fun setEventRead(isRead: Long, date: String)
    fun clearAllEvents()
}
