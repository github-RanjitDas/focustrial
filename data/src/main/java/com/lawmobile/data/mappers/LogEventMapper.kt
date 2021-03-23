package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainNotification
import com.lawmobile.domain.enums.LogEventType
import com.lawmobile.domain.enums.NotificationType
import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent

object LogEventMapper {
    fun cameraToDomainNotificationList(logEventList: List<LogEvent>) =
        logEventList.map { cameraToDomainNotification(it) }

    private fun cameraToDomainNotification(logEvent: LogEvent) =
        logEvent.run {
            val notificationType = when (name) {
                LogEventType.NOTIFICATION.value -> {
                    when (type.split(":").first()) {
                        NotificationType.WARNING.value -> NotificationType.WARNING
                        NotificationType.ERROR.value -> NotificationType.ERROR
                        else -> NotificationType.INFORMATION
                    }
                }
                LogEventType.CAMERA.value -> NotificationType.INFORMATION
                else -> NotificationType.INFORMATION
            }
            DomainNotification(
                name = type.split(":").last(),
                date = date,
                type = notificationType,
                value = value
            )
        }
}
