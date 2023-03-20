package com.lawmobile.data.repository.events

import com.lawmobile.data.datasource.local.events.EventsLocalDataSource
import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSource
import com.lawmobile.data.mappers.impl.CameraEventMapper.toDomainList
import com.lawmobile.data.mappers.impl.CameraEventMapper.toLocal
import com.lawmobile.data.mappers.impl.CameraEventMapper.toLocalList
import com.lawmobile.domain.NotificationDictionary
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventType
import com.lawmobile.domain.extensions.simpleDateFormat
import com.lawmobile.domain.repository.events.EventsRepository
import com.lawmobile.domain.utils.DateHelper
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts

class EventsRepositoryImpl(
    private val eventsRemoteDataSource: EventsRemoteDataSource,
    private val eventsLocalDataSource: EventsLocalDataSource
) : EventsRepository {

    override suspend fun saveEvent(cameraEvent: CameraEvent) {
        val localEvent = cameraEvent.toLocal()
        eventsLocalDataSource.saveEvent(localEvent)
    }

    override suspend fun getNotificationDictionary(): Result<List<NotificationDictionary>> {
        val result = getResultWithAttempts(ATTEMPTS_TO_GET_CAMERA_EVENTS, ATTEMPTS_DELAY) {
            eventsRemoteDataSource.getNotificationDictionary()
        }
        return when (result) {
            is Result.Success -> {
                val transformedList = result.data.map {
                    NotificationDictionary(it.id, it.name, it.type, it.value, it.note)
                }
                Result.Success(transformedList)
            }
            is Result.Error -> {
                Result.Error(result.exception)
            }
        }
    }

    override suspend fun getCameraEvents(): Result<List<CameraEvent>> {

        // val todayDate = DateHelper.getTodayDateAtStartOfTheDay()
        // Clear all previous events.
        eventsLocalDataSource.clearAllEvents()
        // if (removeResult is Result.Error) return removeResult

        val result = getResultWithAttempts(ATTEMPTS_TO_GET_CAMERA_EVENTS, ATTEMPTS_DELAY) {
            eventsRemoteDataSource.getCameraEvents()
        }
        return when (result) {
            is Result.Success -> {
                val remoteEventList = result.data
                    .toDomainList()
                    .distinct()
                    .toList()
                    .filter { it.eventType == EventType.NOTIFICATION }
                    .let { manageEventsReadStatus(it) }
                val saveResult = saveEventsInLocal(remoteEventList)
                return if (saveResult is Result.Error) saveResult
                else {
                    areCameraEventsRetrieved = true
                    Result.Success(remoteEventList)
                }
            }

            is Result.Error -> result
        }
    }

    override suspend fun getNotificationEvents(): Result<List<CameraEvent>> {
        if (!areCameraEventsRetrieved) {
            val resultRemoteEvents = getCameraEvents()
            resultRemoteEvents.doIfSuccess {
                areCameraEventsRetrieved = true
                return Result.Success(it)
            }

            return Result.Error(Exception("Error to retrieving camera events"))
        }

        return getNotificationEventsResult()
    }

    private suspend fun getNotificationEventsResult(): Result<List<CameraEvent>> {
        val todayDate = DateHelper.getTodayDateAtStartOfTheDay()
        return when (val result = eventsLocalDataSource.getNotificationEvents(todayDate)) {
            is Result.Success -> Result.Success(result.data.toDomainList())
            is Result.Error -> result
        }
    }

    override suspend fun getPendingNotificationsCount(): Result<Int> {
        if (!areCameraEventsRetrieved) {
            val resultRemoteEvents = getCameraEvents()
            resultRemoteEvents.doIfSuccess {
                areCameraEventsRetrieved = true
                return Result.Success(it.size)
            }

            return Result.Error(Exception("Error to retrieving camera events"))
        }

        return eventsLocalDataSource.getPendingNotificationsCount()
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
                        localEvent.date == remoteEvent.date.simpleDateFormat()
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

    private suspend fun saveEventsInLocal(remoteEventList: List<CameraEvent>): Result<Unit> =
        if (cameraHasNewEvents(remoteEventList)) {
            if (databaseEventsAreNotEmpty()) clearAllEvents()
            saveEventsResult(remoteEventList)
        } else Result.Success(Unit)

    private suspend fun cameraHasNewEvents(notificationList: List<CameraEvent>) =
        notificationList.size > eventsLocalDataSource.getEventsCount()

    private suspend fun databaseEventsAreNotEmpty() = eventsLocalDataSource.getEventsCount() != 0

    private suspend fun saveEventsResult(remoteEventList: List<CameraEvent>): Result<Unit> {
        val localEventList = remoteEventList.toLocalList()
        return when (val result = eventsLocalDataSource.saveAllEvents(localEventList)) {
            is Result.Success -> {
                Result.Success(Unit)
            }
            is Result.Error -> {
                result
            }
        }
    }

    companion object {
        private const val ATTEMPTS_TO_GET_CAMERA_EVENTS = 3
        private const val ATTEMPTS_DELAY = 500L
        private var areCameraEventsRetrieved = false
    }
}
