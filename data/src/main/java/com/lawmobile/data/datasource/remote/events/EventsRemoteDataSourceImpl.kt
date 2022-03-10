package com.lawmobile.data.datasource.remote.events

import com.lawmobile.body_cameras.entities.LogEvent
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result

class EventsRemoteDataSourceImpl(
    cameraServiceFactory: CameraServiceFactory
) : EventsRemoteDataSource {
    private val cameraService = cameraServiceFactory.create()
    override suspend fun getCameraEvents(): Result<List<LogEvent>> = cameraService.getLogEvents()
    override fun isPossibleToReadLog() = cameraService.getCanReadNotification()
}
