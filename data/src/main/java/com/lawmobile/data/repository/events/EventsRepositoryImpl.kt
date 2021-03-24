package com.lawmobile.data.repository.events

import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSource
import com.lawmobile.data.mappers.LogEventMapper
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.repository.events.EventsRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class EventsRepositoryImpl(
    private val eventsRemoteDataSource: EventsRemoteDataSource
) : EventsRepository {

    override suspend fun getLogEvents(): Result<List<CameraEvent>> {
        return when (val result = eventsRemoteDataSource.getLogEvents()) {
            is Result.Success -> {
                val notificationList = LogEventMapper.cameraToDomainNotificationList(result.data)
                if (cameraHasNewEvents(notificationList)) {
                    if (isEventListNotEmpty()) {
                        CameraInfo.cameraEventList = notificationList
                        return Result.Success(notificationList)
                    } else {
                        CameraInfo.cameraEventList = notificationList
                    }
                }
                Result.Error(Exception("There are no new events"))
            }
            is Result.Error -> result
        }
    }

    private fun cameraHasNewEvents(notificationList: List<CameraEvent>) = notificationList.size > CameraInfo.cameraEventList.size
    private fun isEventListNotEmpty() = CameraInfo.cameraEventList.isNotEmpty()

    override fun getNotificationList(): List<CameraEvent> =
        CameraInfo.cameraEventList.filter { it.eventType == EventType.NOTIFICATION }

    override fun isPossibleToReadLog() = eventsRemoteDataSource.isPossibleToReadLog()
}
