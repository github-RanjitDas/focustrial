package com.lawmobile.data.mappers

import com.safefleet.mobile.external_hardware.cameras.entities.CameraUser
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserMapperTest {

    @Test
    fun cameraToDomain() {
        val cameraConnectUserResponse = mockk<CameraUser>(relaxed = true)
        val domainUserResponse = UserMapper.cameraToDomain(cameraConnectUserResponse)
        with(cameraConnectUserResponse){
            domainUserResponse.let {
                assertTrue(it.id == officerId)
                assertTrue(it.name == name)
                assertTrue(it.password == password)
            }
        }
    }
}