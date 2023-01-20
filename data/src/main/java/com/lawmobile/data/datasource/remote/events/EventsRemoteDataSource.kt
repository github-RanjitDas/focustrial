package com.lawmobile.data.datasource.remote.events

import com.lawmobile.body_cameras.entities.LogEvent
import com.lawmobile.body_cameras.entities.NotificationDictionary
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface EventsRemoteDataSource {
    suspend fun getCameraEvents(): Result<List<LogEvent>>
    suspend fun getNotificationDictionary(): Result<List<NotificationDictionary>>
    fun isPossibleToReadLog(): Boolean
}
