package com.lawmobile.data.repository.events

import com.lawmobile.data.datasource.local.events.EventsLocalDataSource
import com.lawmobile.data.datasource.remote.events.EventsRemoteDataSource
import com.lawmobile.data.mappers.CameraEventMapper
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.repository.events.EventsRepository
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import java.text.SimpleDateFormat
import java.util.Locale

class EventsRepositoryImpl(
    private val eventsRemoteDataSource: EventsRemoteDataSource,
    private val eventsLocalDataSource: EventsLocalDataSource
) : EventsRepository {

    override suspend fun saveEvent(cameraEvent: CameraEvent) {
        val localEvent = CameraEventMapper.domainToLocal(cameraEvent)
        eventsLocalDataSource.saveEvent(localEvent)
    }

    override suspend fun deleteOutdatedEvents(date: String): Result<Unit> =
        eventsLocalDataSource.deleteOutdatedEvents(date)

    override suspend fun getCameraEvents(): Result<List<CameraEvent>> {
        val removeResult = removeOutdatedEventsFromDB()
        if (removeResult is Result.Error) return removeResult

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

                val saveResult = saveEventsInLocal(remoteEventList)
                return if (saveResult is Result.Error) saveResult
                else Result.Success(remoteEventList)
            }

            is Result.Error -> result
        }
    }

    private suspend fun removeOutdatedEventsFromDB(): Result<Unit> {
        val format = SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH)
        val currentDate: String = format.format(System.currentTimeMillis()) + " 00:00:00"
        return deleteOutdatedEvents(currentDate)
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

    private suspend fun saveEventsInLocal(remoteEventList: List<CameraEvent>): Result<Unit> =
        if (cameraHasNewEvents(remoteEventList)) {
            if (databaseEventsAreNotEmpty()) clearAllEvents()
            saveEventsResult(remoteEventList)
        } else Result.Success(Unit)

    private fun cameraHasNewEvents(notificationList: List<CameraEvent>) =
        notificationList.size > eventsLocalDataSource.getEventsCount()

    private fun databaseEventsAreNotEmpty() = eventsLocalDataSource.getEventsCount() != 0

    private suspend fun saveEventsResult(remoteEventList: List<CameraEvent>): Result<Unit> {
        val localEventList = CameraEventMapper.domainToLocalList(remoteEventList)

        return when (val result = eventsLocalDataSource.saveAllEvents(localEventList)) {
            is Result.Success -> Result.Success(Unit)
            is Result.Error -> result
        }
    }
}
