package com.lawmobile.data.datasource.local.events

import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface EventsLocalDataSource {
    suspend fun getAllEvents(): Result<List<LocalCameraEvent>>
    suspend fun getLastEvent(): Result<LocalCameraEvent>
    suspend fun saveAllEvents(events: List<LocalCameraEvent>): Result<Unit>
    suspend fun setEventRead(isRead: Boolean, date: String): Result<Unit>
    suspend fun clearAllEvents(): Result<Unit>
}
