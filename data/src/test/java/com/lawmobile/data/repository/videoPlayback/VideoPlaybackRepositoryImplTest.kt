package com.lawmobile.data.repository.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.data.mappers.VideoInformationMapper
import com.lawmobile.data.repository.videoPlayback.VideoPlaybackRepositoryImpl.Companion.ERROR_TO_GET_VIDEO
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainVideoMetadata
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
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)
        val domainInformationVideo = DomainInformationVideo(1, "", 1, "")
        val cameraConnectVideoInformationVideo = CameraConnectVideoInfo(1, 1, 1, "", "", 1, "", "")

        coEvery { videoPlayBackRemoteDataSource.getInformationResourcesVideo(any()) } returns
                Result.Success(cameraConnectVideoInformationVideo)

        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.getInformationResourcesVideo(domainCameraFile) as Result.Success
            Assert.assertEquals(result.data, domainInformationVideo)
        }

        coVerify { videoPlayBackRemoteDataSource.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testGetInformationResourcesVideoSuccessMapperFailed() {
        mockkObject(VideoInformationMapper)
        every { VideoInformationMapper.cameraToDomain(any()) } throws Exception()

        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)
        val cameraConnectVideoInformationVideo = CameraConnectVideoInfo(1, 1, 1, "", "", 1, "", "")

        coEvery { videoPlayBackRemoteDataSource.getInformationResourcesVideo(any()) } returns
                Result.Success(cameraConnectVideoInformationVideo)

        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.getInformationResourcesVideo(domainCameraFile) as Result.Error
            Assert.assertEquals(result.exception.message, ERROR_TO_GET_VIDEO)
        }

        coVerify { videoPlayBackRemoteDataSource.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testGetInformationResourcesVideoError() {
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        coEvery {
            videoPlayBackRemoteDataSource.getInformationResourcesVideo(any())
        } returns Result.Error(mockk())

        runBlocking {
            val result = videoPlaybackRepositoryImpl.getInformationResourcesVideo(domainCameraFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { videoPlayBackRemoteDataSource.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testSaveVideoMetadataSuccess() {
        val domainVideoMetadata: DomainVideoMetadata = mockk(relaxed = true)

        coEvery {
            videoPlayBackRemoteDataSource.saveVideoMetadata(any())
        } returns Result.Success(Unit)

        runBlocking {
            val result = videoPlaybackRepositoryImpl.saveVideoMetadata(domainVideoMetadata)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify { videoPlayBackRemoteDataSource.saveVideoMetadata(any()) }
    }

    @Test
    fun testSaveVideoMetadataError() {
        val domainVideoMetadata: DomainVideoMetadata = mockk(relaxed = true)
        mockkObject(VideoListMetadata)

        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        coEvery {
            videoPlayBackRemoteDataSource.saveVideoMetadata(any())
        } returns Result.Error(mockk())

        runBlocking {
            val result = videoPlaybackRepositoryImpl.saveVideoMetadata(domainVideoMetadata)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { videoPlayBackRemoteDataSource.saveVideoMetadata(any()) }
    }

    @Test
    fun testGetVideoMetadataSuccessSave() {
        val cameraConnectVideoMetadata: CameraConnectVideoMetadata = mockk(relaxed = true)

        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        coEvery {
            videoPlayBackRemoteDataSource.getVideoMetadata(any(), any())
        } returns Result.Success(cameraConnectVideoMetadata)

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
            videoPlayBackRemoteDataSource.getVideoMetadata(any(), any())
        } returns Result.Error(mockk())

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
            videoPlayBackRemoteDataSource.getVideoMetadata(any(), any())
        } returns Result.Success(cameraConnectVideoMetadata)

        runBlocking {
            val result = videoPlaybackRepositoryImpl.getVideoMetadata("", "")
            Assert.assertTrue(result is Result.Success)
        }
    }
}