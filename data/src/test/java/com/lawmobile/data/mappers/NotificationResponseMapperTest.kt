package com.lawmobile.data.mappers

import com.lawmobile.data.utils.DateHelper
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import com.safefleet.mobile.external_hardware.cameras.entities.NotificationResponse
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert
import org.junit.jupiter.api.Test

class NotificationResponseMapperTest {

    @Test
    fun testCameraToDomain() {
        mockkObject(DateHelper)
        every { DateHelper.dateToString(any(), any(), any()) } returns "07/22/2020"
        val cameraNotification = NotificationResponse("7", "low_storage_warning", "value")
        val response = NotificationResponseMapper.cameraToDomain(cameraNotification)
        Assert.assertEquals(
            response,
            CameraEvent(
                name = "low_storage_warning",
                eventType = EventType.NOTIFICATION,
                eventTag = EventTag.INFORMATION,
                date = "07/22/2020",
                value = "value"
            )
        )
    }
}
