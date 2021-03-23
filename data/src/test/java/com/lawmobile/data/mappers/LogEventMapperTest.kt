package com.lawmobile.data.mappers

import com.lawmobile.domain.enums.NotificationType
import com.safefleet.mobile.external_hardware.cameras.entities.LogEvent
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class LogEventMapperTest {

    @Test
    fun cameraToDomainNotificationListNotificationWarning() {
        val logEventList = listOf(
            LogEvent(
                name = "Notification",
                date = "20/12/2020",
                type = "warn:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )
        val domainNotificationList = LogEventMapper.cameraToDomainNotificationList(logEventList)

        domainNotificationList.forEachIndexed { index, domain ->
            logEventList.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, true
                )
                Assert.assertEquals(
                    domain.name, camera[index].type.split(":").last()
                )
                Assert.assertEquals(
                    domain.type, NotificationType.WARNING
                )
            }
        }
    }

    @Test
    fun cameraToDomainNotificationListNotificationError() {
        val logEventList = listOf(
            LogEvent(
                name = "Notification",
                date = "20/12/2020",
                type = "err:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )
        val domainNotificationList = LogEventMapper.cameraToDomainNotificationList(logEventList)

        domainNotificationList.forEachIndexed { index, domain ->
            logEventList.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, true
                )
                Assert.assertEquals(
                    domain.name, camera[index].type.split(":").last()
                )
                Assert.assertEquals(
                    domain.type, NotificationType.ERROR
                )
            }
        }
    }

    @Test
    fun cameraToDomainNotificationListNotificationInformation() {
        val logEventList = listOf(
            LogEvent(
                name = "Notification",
                date = "20/12/2020",
                type = "inf:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )
        val domainNotificationList = LogEventMapper.cameraToDomainNotificationList(logEventList)

        domainNotificationList.forEachIndexed { index, domain ->
            logEventList.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, true
                )
                Assert.assertEquals(
                    domain.name, camera[index].type.split(":").last()
                )
                Assert.assertEquals(
                    domain.type, NotificationType.INFORMATION
                )
            }
        }
    }

    @Test
    fun cameraToDomainNotificationListCameraInformation() {
        val logEventList = listOf(
            LogEvent(
                name = "Camera",
                date = "20/12/2020",
                type = "inf:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )
        val domainNotificationList = LogEventMapper.cameraToDomainNotificationList(logEventList)

        domainNotificationList.forEachIndexed { index, domain ->
            logEventList.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, true
                )
                Assert.assertEquals(
                    domain.name, camera[index].type.split(":").last()
                )
                Assert.assertEquals(
                    domain.type, NotificationType.INFORMATION
                )
            }
        }
    }

    @Test
    fun cameraToDomainNotificationListOtherInformation() {
        val logEventList = listOf(
            LogEvent(
                name = "Info",
                date = "20/12/2020",
                type = "inf:Low battery",
                value = "Charge your camera",
                additionalInformation = null
            )
        )
        val domainNotificationList = LogEventMapper.cameraToDomainNotificationList(logEventList)

        domainNotificationList.forEachIndexed { index, domain ->
            logEventList.let { camera ->
                Assert.assertEquals(
                    domain.value, camera[index].value
                )
                Assert.assertEquals(
                    domain.date, camera[index].date
                )
                Assert.assertEquals(
                    domain.isRead, true
                )
                Assert.assertEquals(
                    domain.name, camera[index].type.split(":").last()
                )
                Assert.assertEquals(
                    domain.type, NotificationType.INFORMATION
                )
            }
        }
    }
}
