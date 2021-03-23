package com.lawmobile.domain.repository.notification

import com.lawmobile.domain.entities.DomainNotification
import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface NotificationRepository : BaseRepository {
    suspend fun getLogEvents(): Result<List<DomainNotification>>
    fun isPossibleToReadLog(): Boolean
}
