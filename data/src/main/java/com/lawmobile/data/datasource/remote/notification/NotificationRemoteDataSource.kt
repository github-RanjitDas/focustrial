package com.lawmobile.data.datasource.remote.notification

import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface NotificationRemoteDataSource {
    suspend fun getLogEvents(): Result<List<LogEvent>>
    fun isPossibleToReadLog(): Boolean
}
