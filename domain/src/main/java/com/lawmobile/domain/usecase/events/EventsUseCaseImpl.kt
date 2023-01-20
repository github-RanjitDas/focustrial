package com.lawmobile.domain.usecase.events

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.repository.events.EventsRepository

class EventsUseCaseImpl(
    private val eventsRepository: EventsRepository
) : EventsUseCase {
    override suspend fun saveEvent(cameraEvent: CameraEvent) = eventsRepository.saveEvent(cameraEvent)
    override suspend fun getCameraEvents() = eventsRepository.getCameraEvents()
    override suspend fun getNotificationDictionary() = eventsRepository.getNotificationDictionary()
    override suspend fun getNotificationEvents() = eventsRepository.getNotificationEvents()
    override fun isPossibleToReadLog() = eventsRepository.isPossibleToReadLog()
    override suspend fun setAllNotificationsAsRead() = eventsRepository.setAllNotificationsAsRead()
    override suspend fun getPendingNotificationsCount() =
        eventsRepository.getPendingNotificationsCount()
}
