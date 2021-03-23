package com.lawmobile.data.datasource.remote.notification

import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent
import com.safefleet.mobile.kotlin_commons.helpers.Result

class NotificationRemoteDataSourceImpl(
    cameraServiceFactory: CameraServiceFactory
) : NotificationRemoteDataSource {
    private val cameraService = cameraServiceFactory.create()
    override suspend fun getLogEvents(): Result<List<LogEvent>> = cameraService.getLogEvents()
    override fun isPossibleToReadLog() = cameraService.getCanReadNotification()
}
