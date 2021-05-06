package com.lawmobile.data.datasource.remote.events

import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface EventsRemoteDataSource {
    suspend fun getCameraEvents(): Result<List<LogEvent>>
    fun isPossibleToReadLog(): Boolean
}
