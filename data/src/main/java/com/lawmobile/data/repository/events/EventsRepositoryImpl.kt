package com.lawmobile.data.repository.events

import com.lawmobile.data.datasource.local.events.EventsLocalDataSource
import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSource
import com.lawmobile.data.mappers.CameraEventMapper
import com.lawmobile.data.utils.DateHelper
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.extensions.simpleDateFormat
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
        val todayDate = DateHelper.getTodayDateAtStartOfTheDay()
        val removeResult = eventsLocalDataSource.deleteOutdatedEvents(todayDate)
        if (removeResult is Result.Error) return removeResult

        val result = getResultWithAttempts(ATTEMPTS_TO_GET_CAMERA_EVENTS, ATTEMPTS_DELAY) {
            eventsRemoteDataSource.getCameraEvents()
        }

        return when (result) {
            is Result.Success -> {
                val remoteEventList = CameraEventMapper
                    .cameraToDomainList(result.data)
                    .distinct()
                    .toList()
                    .filter { it.date.simpleDateFormat() >= DateHelper.getTodayDateAtStartOfTheDay() }
                    .let { manageEventsReadStatus(it) }

                val saveResult = saveEventsInLocal(remoteEventList)
                return if (saveResult is Result.Error) saveResult
                else Result.Success(remoteEventList)
            }

            is Result.Error -> result
        }
    }

    override suspend fun getNotificationEvents(): Result<List<CameraEvent>> {
        return when (val result = eventsLocalDataSource.getNotificationEvents(DateHelper.getTodayDateAtStartOfTheDay())) {
            is Result.Success -> Result.Success(CameraEventMapper.localToDomainList(result.data))
            is Result.Error -> result
        }
    }

    override suspend fun getPendingNotificationsCount(): Result<Int> {
        if (!areCameraEventsRetrieved) {
            areCameraEventsRetrieved = true
            val resultRemoteEvents = getCameraEvents()
            resultRemoteEvents.doIfSuccess {
                return eventsLocalDataSource.getPendingNotificationsCount()
            }

            return Result.Error(Exception("Error to get pending notification"))
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
        val localEventList = CameraEventMapper.domainToLocalList(remoteEventList)
        return when (val result = eventsLocalDataSource.saveAllEvents(localEventList)) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> result
        }
    }

    companion object {
        const val ATTEMPTS_TO_GET_CAMERA_EVENTS = 3
        const val ATTEMPTS_DELAY = 500L
        var areCameraEventsRetrieved = false
    }
}
