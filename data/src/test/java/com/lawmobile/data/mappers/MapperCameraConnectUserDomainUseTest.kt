package com.lawmobile.data.mappers

import com.lawmobile.domain.entity.DomainUser
import com.safefleet.mobile.avml.cameras.entities.CameraConnectUserResponse
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MapperCameraConnectUserDomainUseTest {


    @Test
    fun testCameraConnectUserResponseToDomainUser() {
        val domainUser = DomainUser("123", "first name last name", "password")
        val domainUserResponse = MapperCameraConnectUserDomainUse.cameraConnectUserResponseToDomainUser(
            CameraConnectUserResponse("123", "first name last name", "password")
        )
        Assert.assertEquals(domainUser,domainUserResponse)
    }

}