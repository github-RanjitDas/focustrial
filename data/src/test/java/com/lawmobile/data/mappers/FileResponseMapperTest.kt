package com.lawmobile.data.mappers

import com.lawmobile.data.extensions.getCreationDate
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class FileResponseMapperTest {

    @Test
    fun cameraToDomain() {
        val cameraConnectResponse: CameraConnectFileResponseWithErrors = mockk(relaxed = true) {
            every { items } returns arrayListOf(
                CameraConnectFile("", "3", "", ""),
                CameraConnectFile("", "1", "", ""),
                CameraConnectFile("", "6", "", ""),
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