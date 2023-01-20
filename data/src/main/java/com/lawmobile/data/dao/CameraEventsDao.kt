package com.lawmobile.data.dao

import com.lawmobile.data.dao.entities.LocalCameraEvent

interface CameraEventsDao {
    fun getAllEvents(): List<LocalCameraEvent>
    fun getNotificationEvents(date: String): List<LocalCameraEvent>
    fun getEventsCount(): Int
    fun getPendingNotificationsCount(): Int
    fun saveEvent(event: LocalCameraEvent)
    fun setAllNotificationsAsRead()
    fun deleteOutdatedEvents(date: String)
    fun clearAllEvents()
    fun getAllNotificationTillToday(): List<LocalCameraEvent>
}
