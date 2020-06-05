package com.lawmobile.data.repository.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.data.mappers.MapperCameraConnectVideoInfoDomainVideo
import com.lawmobile.data.repository.videoPlayback.VideoPlaybackRepositoryImpl.Companion.ERROR_TO_GET_VIDEO
import com.lawmobile.domain.entity.DomainInformationVideo
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoInfo
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VideoPlaybackRepositoryImplTest {

    private val videoPlayBackRemoteDataSource: VideoPlaybackRemoteDataSource = mockk()
    private val videoPlaybackRepositoryImpl by lazy {
        VideoPlaybackRepositoryImpl(videoPlayBackRemoteDataSource)
    }

    @BeforeEach
    fun setUp() {
        clearAllMocks()
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
    fun testGetInformationResourcesVideoSuccessMapperFailed() {
        mockkObject(MapperCameraConnectVideoInfoDomainVideo)
        every {
            MapperCameraConnectVideoInfoDomainVideo.cameraConnectFileToDomainInformationVideo(
                any()
            )
        } throws Exception()
        val cameraConnectFile: CameraConnectFile = mockk()
        val cameraConnectVideoInformationVideo = CameraConnectVideoInfo(1, 1, 1, "", "", 1, "", "")
        coEvery { videoPlayBackRemoteDataSource.getInformationResourcesVideo(cameraConnectFile) } returns Result.Success(
            cameraConnectVideoInformationVideo
        )
        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.getInformationResourcesVideo(cameraConnectFile) as Result.Error
            Assert.assertEquals(result.exception.message, ERROR_TO_GET_VIDEO)
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

    @Test
    fun testSaveVideoMetadataSuccess() {
        val cameraConnectVideoMetadata: CameraConnectVideoMetadata = mockk {
            every { fileName } returns ""
        }
        coEvery { videoPlayBackRemoteDataSource.saveVideoMetadata(cameraConnectVideoMetadata) } returns Result.Success(
            Unit
        )
        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.saveVideoMetadata(cameraConnectVideoMetadata)
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { videoPlayBackRemoteDataSource.saveVideoMetadata(cameraConnectVideoMetadata) }
    }

    @Test
    fun testSaveVideoMetadataError() {
        val cameraConnectVideoMetadata: CameraConnectVideoMetadata = mockk(relaxed = true)
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null

        coEvery { videoPlayBackRemoteDataSource.saveVideoMetadata(cameraConnectVideoMetadata) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.saveVideoMetadata(cameraConnectVideoMetadata)
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { videoPlayBackRemoteDataSource.saveVideoMetadata(cameraConnectVideoMetadata) }
    }

    @Test
    fun testGetVideoMetadataSuccessSave() {
        val cameraConnectVideoMetadata: CameraConnectVideoMetadata = mockk(relaxed = true)
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null

        coEvery {
            videoPlayBackRemoteDataSource.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(
            cameraConnectVideoMetadata
        )
        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.getVideoMetadata("", "")
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { videoPlayBackRemoteDataSource.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testGetVideoMetadataError() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        coEvery {
            videoPlayBackRemoteDataSource.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.getVideoMetadata("", "")
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { videoPlayBackRemoteDataSource.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testGetVideoMetadataSaved() {
        val cameraConnectVideoMetadata: CameraConnectVideoMetadata = mockk(relaxed = true)
        coEvery {
            videoPlayBackRemoteDataSource.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(
            cameraConnectVideoMetadata
        )
        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.getVideoMetadata("", "")
            Assert.assertTrue(result is Result.Success)
        }
    }
}