package com.lawmobile.data.repository.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.domain.entity.DomainInformationVideo
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoInfo
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import  com.safefleet.mobile.commons.helpers.Result
import io.mockk.coVerify
import kotlinx.coroutines.runBlocking
import org.junit.Assert

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VideoPlaybackRepositoryImplTest {

    private val videoPlayBackRemoteDataSource: VideoPlaybackRemoteDataSource = mockk()
    private val videoPlaybackRepositoryImpl by lazy {
        VideoPlaybackRepositoryImpl(videoPlayBackRemoteDataSource)
    }


    @Test
    fun testGetInformationResourcesVideoSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        val domainInformationVideo = DomainInformationVideo(1, "", 1, "")
        val cameraConnectVideoInformationVideo = CameraConnectVideoInfo(1, 1, 1, "", "", 1, "", "")
        coEvery { videoPlayBackRemoteDataSource.getInformationResourcesVideo(cameraConnectFile) } returns Result.Success(
            cameraConnectVideoInformationVideo
        )
        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.getInformationResourcesVideo(cameraConnectFile) as Result.Success
            Assert.assertEquals(result.data, domainInformationVideo)
        }

        coVerify { videoPlayBackRemoteDataSource.getInformationResourcesVideo(cameraConnectFile) }
    }

    @Test
    fun testGetInformationResourcesVideoError() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { videoPlayBackRemoteDataSource.getInformationResourcesVideo(cameraConnectFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.getInformationResourcesVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { videoPlayBackRemoteDataSource.getInformationResourcesVideo(cameraConnectFile) }
    }

}