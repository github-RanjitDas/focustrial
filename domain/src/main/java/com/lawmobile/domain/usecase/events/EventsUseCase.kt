package com.lawmobile.domain.usecase.events

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface EventsUseCase : BaseUseCase {
    suspend fun getLogEvents(): Result<List<CameraEvent>>
    fun getNotificationList(): List<CameraEvent>
    fun isPossibleToReadLog(): Boolean
}
