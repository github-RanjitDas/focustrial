package com.lawmobile.data.repository.notification

import com.lawmobile.data.datasource.remote.notification.NotificationRemoteDataSource
import com.lawmobile.data.mappers.LogEventMapper
import com.lawmobile.domain.repository.notification.NotificationRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

class NotificationRepositoryImpl(
    private val notificationRemoteDataSource: NotificationRemoteDataSource
) : NotificationRepository {

    override suspend fun getLogEvents() =
        when (val result = notificationRemoteDataSource.getLogEvents()) {
            is Result.Success -> Result.Success(LogEventMapper.cameraToDomainNotificationList(result.data))
            is Result.Error -> result
        }

    override fun isPossibleToReadLog() = notificationRemoteDataSource.isPossibleToReadLog()
}
