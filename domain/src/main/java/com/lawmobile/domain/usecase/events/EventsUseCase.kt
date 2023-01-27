package com.lawmobile.domain.usecase.events

import com.lawmobile.domain.NotificationDictionary
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface EventsUseCase : BaseUseCase {
    suspend fun saveEvent(cameraEvent: CameraEvent)
    suspend fun getCameraEvents(): Result<List<CameraEvent>>
    suspend fun getNotificationDictionary(): Result<List<NotificationDictionary>>
    suspend fun getNotificationEvents(): Result<List<CameraEvent>>
    suspend fun getPendingNotificationsCount(): Result<Int>
    fun isPossibleToReadLog(): Boolean
    suspend fun setAllNotificationsAsRead()
}
