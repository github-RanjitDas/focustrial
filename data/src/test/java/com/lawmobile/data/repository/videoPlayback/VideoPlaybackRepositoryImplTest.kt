package com.lawmobile.data.repository.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.data.mappers.VideoInformationMapper
import com.lawmobile.data.repository.videoPlayback.VideoPlaybackRepositoryImpl.Companion.ERROR_TO_GET_VIDEO
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.entities.VideoListMetadata
import com.safefleet.mobile.external_hardware.cameras.entities.VideoFileInfo
import com.safefleet.mobile.external_hardware.cameras.entities.VideoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VideoPlaybackRepositoryImplTest {

    private val videoPlayBackRemoteDataSource: VideoPlaybackRemoteDataSource = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val videoPlaybackRepositoryImpl by lazy {
        VideoPlaybackRepositoryImpl(videoPlayBackRemoteDataSource)
    }

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        clearAllMocks()
    }

    @Test
    fun testGetInformationResourcesVideoSuccess() = runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)
        val domainInformationVideo = DomainInformationVideo(1, "", 1, "")
        val cameraConnectVideoInformationVideo = VideoFileInfo(1, 1, 1, "", "", 1, "", "")

        coEvery { videoPlayBackRemoteDataSource.getInformationResourcesVideo(any()) } returns
            Result.Success(cameraConnectVideoInformationVideo)

        val result =
            videoPlaybackRepositoryImpl.getInformationResourcesVideo(domainCameraFile) as Result.Success
        Assert.assertEquals(result.data, domainInformationVideo)

        coVerify { videoPlayBackRemoteDataSource.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testGetInformationResourcesVideoSuccessMapperFailed() = runBlockingTest {
        mockkObject(VideoInformationMapper)
        every { VideoInformationMapper.cameraToDomain(any()) } throws Exception()

        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)
        val cameraConnectVideoInformationVideo = VideoFileInfo(1, 1, 1, "", "", 1, "", "")

        coEvery { videoPlayBackRemoteDataSource.getInformationResourcesVideo(any()) } returns
            Result.Success(cameraConnectVideoInformationVideo)

        val result =
            videoPlaybackRepositoryImpl.getInformationResourcesVideo(domainCameraFile) as Result.Error
        Assert.assertEquals(result.exception.message, ERROR_TO_GET_VIDEO)

        coVerify { videoPlayBackRemoteDataSource.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testGetInformationResourcesVideoError() = runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        coEvery {
            videoPlayBackRemoteDataSource.getInformationResourcesVideo(any())
        } returns Result.Error(mockk())

        val result = videoPlaybackRepositoryImpl.getInformationResourcesVideo(domainCameraFile)
        Assert.assertTrue(result is Result.Error)

        coVerify { videoPlayBackRemoteDataSource.getInformationResourcesVideo(any()) }
    }

    @Test
    fun testSaveVideoMetadataSuccess() = runBlockingTest {
        val domainVideoMetadata: DomainVideoMetadata = mockk(relaxed = true)

        coEvery {
            videoPlayBackRemoteDataSource.saveVideoMetadata(any())
        } returns Result.Success(Unit)

        val result = videoPlaybackRepositoryImpl.saveVideoMetadata(domainVideoMetadata)
        Assert.assertTrue(result is Result.Success)

        coVerify { videoPlayBackRemoteDataSource.saveVideoMetadata(any()) }
    }

    @Test
    fun testSaveVideoMetadataError() = runBlockingTest {
        val domainVideoMetadata: DomainVideoMetadata = mockk(relaxed = true)
        mockkObject(VideoListMetadata)

        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        coEvery {
            videoPlayBackRemoteDataSource.saveVideoMetadata(any())
        } returns Result.Error(mockk())

        val result = videoPlaybackRepositoryImpl.saveVideoMetadata(domainVideoMetadata)
        Assert.assertTrue(result is Result.Error)

        coVerify { videoPlayBackRemoteDataSource.saveVideoMetadata(any()) }
    }

    @Test
    fun testGetVideoMetadataSuccessSave() = runBlockingTest {
        val cameraConnectVideoMetadata: VideoInformation = mockk(relaxed = true)

        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        coEvery {
            videoPlayBackRemoteDataSource.getVideoMetadata(any(), any())
        } returns Result.Success(cameraConnectVideoMetadata)

        val result = videoPlaybackRepositoryImpl.getVideoMetadata("", "")
        Assert.assertTrue(result is Result.Success)

        coVerify { videoPlayBackRemoteDataSource.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testGetVideoMetadataError() = runBlockingTest {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        coEvery {
            videoPlayBackRemoteDataSource.getVideoMetadata(any(), any())
        } returns Result.Error(mockk())

        val result = videoPlaybackRepositoryImpl.getVideoMetadata("", "")
        Assert.assertTrue(result is Result.Error)

        coVerify { videoPlayBackRemoteDataSource.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testGetVideoMetadataSaved() = runBlockingTest {
        val cameraConnectVideoMetadata: VideoInformation = mockk(relaxed = true)

        coEvery {
            videoPlayBackRemoteDataSource.getVideoMetadata(any(), any())
        } returns Result.Success(cameraConnectVideoMetadata)

        val result = videoPlaybackRepositoryImpl.getVideoMetadata("", "")
        Assert.assertTrue(result is Result.Success)
    }
}
