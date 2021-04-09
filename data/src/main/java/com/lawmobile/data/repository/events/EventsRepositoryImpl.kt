package com.lawmobile.data.repository.events

import com.lawmobile.data.datasource.local.events.EventsLocalDataSource
import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSource
import com.lawmobile.data.mappers.CameraEventMapper
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.repository.events.EventsRepository
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts

class EventsRepositoryImpl(
    private val eventsRemoteDataSource: EventsRemoteDataSource,
    private val eventsLocalDataSource: EventsLocalDataSource
) : EventsRepository {

    override suspend fun saveEvent(cameraEvent: CameraEvent) {
        val localEvent = CameraEventMapper.domainToLocal(cameraEvent)
        eventsLocalDataSource.saveEvent(localEvent)
    }

    override suspend fun getCameraEvents(): Result<List<CameraEvent>> {
        val result = getResultWithAttempts(3, 500) {
            eventsRemoteDataSource.getCameraEvents()
        }
        return when (result) {
            is Result.Success -> {
                val remoteEventList = CameraEventMapper
                    .cameraToDomainList(result.data)
                    .distinct()
                    .toList()
                    .let { manageEventsReadStatus(it) }

                saveEventsInLocal(remoteEventList)

                return Result.Success(remoteEventList)
            }

            is Result.Error -> result
        }
    }

    override suspend fun getNotificationEvents(): Result<List<CameraEvent>> =
        when (val result = eventsLocalDataSource.getNotificationEvents()) {
            is Result.Success -> Result.Success(CameraEventMapper.localToDomainList(result.data))
            is Result.Error -> result
        }

    override suspend fun getPendingNotificationsCount(): Result<Int> {
        val resultRemoteEvents = getCameraEvents()

        resultRemoteEvents.doIfSuccess {
            return eventsLocalDataSource.getPendingNotificationsCount()
        }
        return Result.Error(Exception("Error to get pending notification"))
    }

    override fun isPossibleToReadLog() = eventsRemoteDataSource.isPossibleToReadLog()

    override suspend fun setAllNotificationsAsRead() =
        eventsLocalDataSource.setAllNotificationsAsRead()

    override suspend fun clearAllEvents() = eventsLocalDataSource.clearAllEvents()

    private suspend fun manageEventsReadStatus(eventList: List<CameraEvent>): List<CameraEvent> {
        with(eventsLocalDataSource.getAllEvents()) {
            doIfSuccess {
                eventList.forEach { remoteEvent ->
                    val event = it.firstOrNull { localEvent ->
                        localEvent.date == remoteEvent.date
                    }
                    if (event != null) {
                        remoteEvent.isRead = event.isRead == 1L
                    } else {
                        remoteEvent.isRead = false
                    }
                }
            }
        }
        return eventList
    }

    private suspend fun saveEventsInLocal(remoteEventList: List<CameraEvent>) {
        if (cameraHasNewEvents(remoteEventList)) {
            if (eventsAreNotEmpty()) {
                clearAllEvents()
                saveEventsResult(remoteEventList)
            } else {
                saveEventsResult(remoteEventList)
            }
        }
    }

    private fun cameraHasNewEvents(notificationList: List<CameraEvent>) =
        notificationList.size > eventsLocalDataSource.getEventsCount()

    private fun eventsAreNotEmpty() = eventsLocalDataSource.getEventsCount() != 0

    private suspend fun saveEventsResult(remoteEventList: List<CameraEvent>): Result<List<CameraEvent>> {
        val localEventList = CameraEventMapper.domainToLocalList(remoteEventList)

        return when (val result = eventsLocalDataSource.saveAllEvents(localEventList)) {
            is Result.Success -> Result.Success(remoteEventList)
            is Result.Error -> result
        }
    }
}
