package com.lawmobile.domain.repository.events

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface EventsRepository : BaseRepository {
    suspend fun getLogEvents(): Result<List<CameraEvent>>
    fun getNotificationList(): List<CameraEvent>
    fun isPossibleToReadLog(): Boolean
}
