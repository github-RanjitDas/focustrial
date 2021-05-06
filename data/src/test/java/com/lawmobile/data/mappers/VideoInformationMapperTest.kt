package com.lawmobile.data.mappers

import com.safefleet.mobile.external_hardware.cameras.entities.VideoFileInfo
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class VideoInformationMapperTest {

    @Test
    fun cameraToDomain() {
        val cameraConnectVideoInfo: VideoFileInfo = mockk(relaxed = true)
        val domainInformationVideo = VideoInformationMapper.cameraToDomain(cameraConnectVideoInfo)
        with(cameraConnectVideoInfo) {
            domainInformationVideo.let {
                assertTrue(it.date == date)
                assertTrue(it.duration == duration)
                assertTrue(it.size == size)
                assertTrue(it.urlVideo == urlVideo)
            }
        }
    }
}
