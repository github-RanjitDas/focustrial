package com.lawmobile.data.repository.videoPlayback

import com.lawmobile.data.datasource.remote.videoPlayback.VideoPlaybackRemoteDataSource
import com.lawmobile.data.entities.RemoteCameraMetadata
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
        VideoPlaybackRepositoryImpl.metadataList = mutableListOf(
            RemoteCameraMetadata(
                mockk {
                    every { fileName } returns ""
                },
                true
            )
        )
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
        VideoPlaybackRepositoryImpl.metadataList = mockk(relaxed = true)
        coEvery { videoPlayBackRemoteDataSource.saveVideoMetadata(any()) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.saveVideoMetadata(mockk())
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { videoPlayBackRemoteDataSource.saveVideoMetadata(any()) }
    }

    @Test
    fun testGetVideoMetadataSuccessUpdate() {
        VideoPlaybackRepositoryImpl.metadataList = mutableListOf(
            RemoteCameraMetadata(
                mockk {
                    every { fileName } returns ""
                },
                true
            )
        )
        coEvery {
            videoPlayBackRemoteDataSource.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.getVideoMetadata("", "")
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { videoPlayBackRemoteDataSource.getVideoMetadata(any(), any()) }
    }

    @Test
    fun testGetVideoMetadataSuccessSave() {
        VideoPlaybackRepositoryImpl.metadataList = mutableListOf(
            RemoteCameraMetadata(
                mockk {
                    every { fileName } returns "x"
                },
                true
            )
        )
        coEvery {
            videoPlayBackRemoteDataSource.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(
            mockk()
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
        VideoPlaybackRepositoryImpl.metadataList = mockk(relaxed = true)
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
        VideoPlaybackRepositoryImpl.metadataList = mutableListOf(
            RemoteCameraMetadata(
                mockk {
                    every { fileName } returns ""
                },
                false
            )
        )
        coEvery {
            videoPlayBackRemoteDataSource.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result =
                videoPlaybackRepositoryImpl.getVideoMetadata("", "")
            Assert.assertTrue(result is Result.Success)
        }
    }
}