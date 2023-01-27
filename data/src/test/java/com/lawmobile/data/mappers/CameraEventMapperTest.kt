package com.lawmobile.data.mappers

import com.lawmobile.body_cameras.entities.LogEvent
import com.lawmobile.data.dao.entities.LocalCameraEvent
import com.lawmobile.data.mappers.impl.CameraEventMapper.toDomainList
import com.lawmobile.data.mappers.impl.CameraEventMapper.toLocalList
import com.lawmobile.domain.entities.CameraEvent
import com.lawmobile.domain.enums.EventTag
import com.lawmobile.domain.enums.EventType
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class CameraEventMapperTest {

    @Test
    fun cameraToDomainListNotificationWarning() {
        val logEventList = listOf(
            LogEvent(
                name = "warning",
                date = "20/12/2020",
                type = "warning:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )
        val domainList = logEventList.toDomainList()

        domainList.forEachIndexed { index, domain ->
            logEventList.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, false
                )
                Assert.assertEquals(
                    domain.name, camera[index].type.split(":").last()
                )
                Assert.assertEquals(
                    domain.eventTag, EventTag.WARNING
                )
            }
        }
    }

    @Test
    fun cameraToDomainListNotificationError() {
        val logEventList = listOf(
            LogEvent(
                name = "error",
                date = "20/12/2020",
                type = "error:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )
        val domainList = logEventList.toDomainList()

        domainList.forEachIndexed { index, domain ->
            logEventList.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, false
                )
                Assert.assertEquals(
                    domain.name, camera[index].type.split(":").last()
                )
                Assert.assertEquals(
                    domain.eventTag, EventTag.ERROR
                )
            }
        }
    }

    @Test
    fun cameraToDomainListNotificationInformation() {
        val logEventList = listOf(
            LogEvent(
                name = "information",
                date = "20/12/2020",
                type = "information:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )
        val domainList = logEventList.toDomainList()

        domainList.forEachIndexed { index, domain ->
            logEventList.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, false
                )
                Assert.assertEquals(
                    domain.name, camera[index].type.split(":").last()
                )
                Assert.assertEquals(
                    domain.eventTag, EventTag.INFORMATION
                )
            }
        }
    }

    @Test
    fun cameraToDomainListCameraInformation() {
        val logEventList = listOf(
            LogEvent(
                name = "information",
                date = "20/12/2020",
                type = "information:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )
        val domainList = logEventList.toDomainList()

        domainList.forEachIndexed { index, domain ->
            logEventList.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, false
                )
                Assert.assertEquals(
                    domain.name, camera[index].type.split(":").last()
                )
                Assert.assertEquals(
                    domain.eventTag, EventTag.INFORMATION
                )
            }
        }
    }

    @Test
    fun cameraToDomainListOtherInformation() {
        val logEventList = listOf(
            LogEvent(
                name = "information",
                date = "20/12/2020",
                type = "information:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )
        val domainList = logEventList.toDomainList()

        domainList.forEachIndexed { index, domain ->
            logEventList.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, false
                )
                Assert.assertEquals(
                    domain.name, camera[index].type.split(":").last()
                )
                Assert.assertEquals(
                    domain.eventTag, EventTag.INFORMATION
                )
            }
        }
    }

    @Test
    fun localToDomainList() {
        val localEvent = listOf(
            LocalCameraEvent(
                name = "Low battery",
                date = "20/12/2020",
                eventType = "Camera",
                eventTag = "information",
                value = "Charge your camera",
                isRead = 0
            )
        )
        val domainList = localEvent.toDomainList()

        domainList.forEachIndexed { index, domain ->
            localEvent.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, camera[index].isRead == 1L
                )
                Assert.assertEquals(
                    domain.name, camera[index].name
                )
                Assert.assertEquals(
                    domain.eventType.value, camera[index].eventType
                )
                Assert.assertEquals(
                    domain.eventTag.value, EventTag.INFORMATION.value
                )
            }
        }
    }

    @Test
    fun localToDomainListReadEvent() {
        val localEvent = listOf(
            LocalCameraEvent(
                name = "Low battery",
                date = "20/12/2020",
                eventType = "Camera",
                eventTag = "",
                value = "Charge your camera",
                isRead = 1
            )
        )
        val domainList = localEvent.toDomainList()

        domainList.forEachIndexed { index, domain ->
            localEvent.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, camera[index].isRead == 1L
                )
                Assert.assertEquals(
                    domain.name, camera[index].name
                )
                Assert.assertEquals(
                    domain.eventType.value, camera[index].eventType
                )
                Assert.assertEquals(
                    domain.eventTag.value, EventTag.NOTIFICATION.value
                )
            }
        }
    }

    @Test
    fun domainToLocalList() {
        val domainList = listOf(
            CameraEvent(
                name = "Low battery",
                date = "20/12/2020",
                eventType = EventType.CAMERA,
                eventTag = EventTag.ERROR,
                value = "Charge your camera",
                isRead = true
            )
        )
        val localEvent = domainList.toLocalList()

        domainList.forEachIndexed { index, domain ->
            localEvent.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, camera[index].isRead == 1L
                )
                Assert.assertEquals(
                    domain.name, camera[index].name
                )
                Assert.assertEquals(
                    domain.eventType.value, camera[index].eventType
                )
                Assert.assertEquals(
                    domain.eventTag.value, camera[index].eventTag
                )
            }
        }
    }
}
