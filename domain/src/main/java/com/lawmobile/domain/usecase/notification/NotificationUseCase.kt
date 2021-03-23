package com.lawmobile.domain.usecase.notification

import com.lawmobile.domain.entities.DomainNotification
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.kotlin_commons.helpers.Result

interface NotificationUseCase : BaseUseCase {
    suspend fun getLogEvents(): Result<List<DomainNotification>>
    fun isPossibleToReadLog(): Boolean
}
