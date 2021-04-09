package com.lawmobile.data.dao

import com.lawmobile.data.dao.entities.LocalCameraEvent

interface CameraEventsDao {
    fun getAllEvents(): List<LocalCameraEvent>
    fun getNotificationEvents(): List<LocalCameraEvent>
    fun getEventById(id: Long): LocalCameraEvent
    fun getEventsCount(): Int
    fun getLastEventId(): Long
    fun getPendingNotificationsCount(): Int
    fun saveEvent(event: LocalCameraEvent)
    fun setEventRead(isRead: Long, date: String)
    fun setAllNotificationsAsRead()
    fun clearAllEvents()
}
