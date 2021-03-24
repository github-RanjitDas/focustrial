package com.lawmobile.domain.usecase.events

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.repository.events.EventsRepository

class EventsUseCaseImpl(
    private val eventsRepository: EventsRepository
) : EventsUseCase {
    override suspend fun getLogEvents() = eventsRepository.getLogEvents()
    override fun getNotificationList(): List<CameraEvent> = eventsRepository.getNotificationList()
    override fun isPossibleToReadLog() = eventsRepository.isPossibleToReadLog()
}
