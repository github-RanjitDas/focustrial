package com.lawmobile.data.datasource.local.events

import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface EventsLocalDataSource {
    suspend fun getAllEvents(): Result<List<LocalCameraEvent>>
    suspend fun getAllNotificationEvents(): Result<List<LocalCameraEvent>>
    suspend fun getLastEvent(): Result<LocalCameraEvent>
    suspend fun getPendingNotificationsCount(): Result<Int>
    fun getEventsCount(): Int
    suspend fun saveAllEvents(events: List<LocalCameraEvent>): Result<Unit>
    suspend fun setEventRead(isRead: Boolean, date: String): Result<Unit>
    suspend fun setAllNotificationsAsRead()
    suspend fun clearAllEvents()
}
