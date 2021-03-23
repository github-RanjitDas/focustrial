package com.lawmobile.domain.usecase.notification

import com.lawmobile.domain.repository.notification.NotificationRepository

class NotificationUseCaseImpl(
    private val notificationRepository: NotificationRepository
) : NotificationUseCase {
    override suspend fun getLogEvents() = notificationRepository.getLogEvents()
    override fun isPossibleToReadLog() = notificationRepository.isPossibleToReadLog()
}
