package com.lawmobile.data.datasource.local.events

import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface EventsLocalDataSource {
    suspend fun getAllEvents(): Result<List<LocalCameraEvent>>
    suspend fun getNotificationEvents(date: String): Result<List<LocalCameraEvent>>
    suspend fun getPendingNotificationsCount(): Result<Int>
    suspend fun getEventsCount(): Int
    suspend fun saveEvent(localEvent: LocalCameraEvent)
    suspend fun saveAllEvents(events: List<LocalCameraEvent>): Result<Unit>
    suspend fun setAllNotificationsAsRead()
    suspend fun deleteOutdatedEvents(date: String): Result<Unit>
    suspend fun clearAllEvents()
}
