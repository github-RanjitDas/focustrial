package com.lawmobile.data.datasource.remote.videoPlayback

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VideoPlaybackRemoteDataSourceImplTest {

    private val cameraConnectService: CameraConnectService = mockk()
    private val videoPlaybackRemoteDataSourceImpl by lazy {
        VideoPlaybackRemoteDataSourceImpl(cameraConnectService)
    }


    @Test
    fun testGetInformationResourcesVideoSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { cameraConnectService.getInformationResourcesVideo(cameraConnectFile) } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify { cameraConnectService.getInformationResourcesVideo(cameraConnectFile) }
    }

    @Test
    fun testGetInformationResourcesVideoError() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { cameraConnectService.getInformationResourcesVideo(cameraConnectFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { cameraConnectService.getInformationResourcesVideo(cameraConnectFile) }
    }
}