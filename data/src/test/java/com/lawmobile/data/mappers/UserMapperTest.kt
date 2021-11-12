package com.lawmobile.data.mappers

import com.lawmobile.data.dto.entities.UserDto
import com.lawmobile.data.mappers.impl.UserMapper.toDomain
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
        val domainUserResponse = cameraConnectUserResponse.toDomain()
        with(cameraConnectUserResponse) {
            domainUserResponse.let {
                assertTrue(it.id == officerId)
                assertTrue(it.name == name)
                assertTrue(it.password == password)
            }
        }
    }

    @Test
    fun dtoToDomain() {
        val userDto = mockk<UserDto>(relaxed = true)
        val user = userDto.toDomain()
        with(userDto) {
            user.let {
                assertTrue(it.email == email)
                assertTrue(it.devicePassword == devicePassword)
            }
        }
    }
}
