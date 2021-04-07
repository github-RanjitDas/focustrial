package com.lawmobile.domain.usecase.events

import com.lawmobile.domain.repository.events.EventsRepository

class EventsUseCaseImpl(
    private val eventsRepository: EventsRepository
) : EventsUseCase {
    override suspend fun getCameraEvents() = eventsRepository.getCameraEvents()
    override suspend fun getAllNotificationEvents() = eventsRepository.getAllNotificationEvents()
    override fun isPossibleToReadLog() = eventsRepository.isPossibleToReadLog()
    override suspend fun setAllNotificationsAsRead() = eventsRepository.setAllNotificationsAsRead()
    override suspend fun clearAllEvents() = eventsRepository.clearAllEvents()
    override suspend fun getPendingNotificationsCount() =
        eventsRepository.getPendingNotificationsCount()
}
