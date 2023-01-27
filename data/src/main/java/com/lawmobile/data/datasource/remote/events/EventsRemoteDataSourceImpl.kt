package com.lawmobile.data.datasource.remote.events

import com.lawmobile.body_cameras.entities.LogEvent
import com.lawmobile.body_cameras.entities.NotificationDictionary
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class EventsRemoteDataSourceImpl(
    cameraServiceFactory: CameraServiceFactory
) : EventsRemoteDataSource {
    private val cameraService = cameraServiceFactory.create()
    override suspend fun getCameraEvents(): Result<List<LogEvent>> = cameraService.getLogEvents()
    override suspend fun getNotificationDictionary(): Result<List<NotificationDictionary>> {
        return cameraService.getNotificationDictionary()
    }
    override fun isPossibleToReadLog() = cameraService.getCanReadNotification()
}
