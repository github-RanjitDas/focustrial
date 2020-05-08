package com.lawmobile.data.datasource.remote.videoPlayback

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.external.CameraDataSource
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

    private val cameraDataSource: CameraDataSource = mockk()
    private val videoPlaybackRemoteDataSourceImpl by lazy {
        VideoPlaybackRemoteDataSourceImpl(cameraDataSource)
    }


    @Test
    fun testGetInformationResourcesVideoSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { cameraDataSource.getInformationResourcesVideo(cameraConnectFile) } returns Result.Success(mockk())
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify { cameraDataSource.getInformationResourcesVideo(cameraConnectFile) }
    }

    @Test
    fun testGetInformationResourcesVideoError() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { cameraDataSource.getInformationResourcesVideo(cameraConnectFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRemoteDataSourceImpl.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { cameraDataSource.getInformationResourcesVideo(cameraConnectFile) }
    }
}