package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.DomainNotification
import com.lawmobile.domain.enums.NotificationType
import com.safefleet.mobile.external_hardware.cameras.entities.NotificationResponse
import org.junit.Assert
import org.junit.jupiter.api.Test

class NotificationResponseMapperTest {

    @Test
    fun testCameraToDomain() {
        val cameraNotification = NotificationResponse("7", "Information", "value")
        val response = NotificationResponseMapper.cameraToDomain(cameraNotification)
        Assert.assertEquals(
            response,
            DomainNotification(
                name = "Information",
                type = NotificationType.INFORMATION,
                value = "value"
            )
        )
    }
}
