package com.lawmobile.data.mappers

import com.lawmobile.data.extensions.getCreationDate
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class FileResponseMapperTest {

    @Test
    fun cameraToDomain() {
        val cameraConnectResponse: FileResponseWithErrors = mockk(relaxed = true) {
            every { items } returns arrayListOf(
                CameraFile("", "3", "", ""),
                CameraFile("", "1", "", ""),
                CameraFile("", "6", "", ""),
            )
        }
        val domainResponse = FileResponseMapper.cameraToDomain(cameraConnectResponse)
        with(cameraConnectResponse) {
            domainResponse.let { response ->
                assertTrue(
                    response.items.map { it.domainCameraFile.date }
                        == items.sortedByDescending { it.getCreationDate() }.map { it.date }
                )
                assertTrue(response.errors == errors)
            }
        }
    }
}