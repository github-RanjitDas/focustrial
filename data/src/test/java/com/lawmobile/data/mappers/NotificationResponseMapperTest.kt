package com.lawmobile.data.mappers

import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
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
            CameraEvent(
                name = "Information",
                eventType = EventType.NOTIFICATION,
                eventTag = EventTag.INFORMATION,
                value = "value"
            )
        )
    }
}
