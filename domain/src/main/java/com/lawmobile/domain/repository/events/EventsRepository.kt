package com.lawmobile.domain.repository.events

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface EventsRepository : BaseRepository {
    suspend fun saveEvent(cameraEvent: CameraEvent)
    suspend fun getCameraEvents(): Result<List<CameraEvent>>
    suspend fun getNotificationEvents(): Result<List<CameraEvent>>
    suspend fun getPendingNotificationsCount(): Result<Int>
    fun isPossibleToReadLog(): Boolean
    suspend fun setAllNotificationsAsRead()
    suspend fun clearAllEvents()
}
