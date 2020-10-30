package com.lawmobile.data.mappers

import com.safefleet.mobile.avml.cameras.entities.CameraConnectUserResponse
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserMapperTest {

    @Test
    fun cameraToDomain() {
        val cameraConnectUserResponse = mockk<CameraConnectUserResponse>(relaxed = true)
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