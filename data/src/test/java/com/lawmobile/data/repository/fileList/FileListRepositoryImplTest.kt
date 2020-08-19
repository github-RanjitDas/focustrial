package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.entities.RemoteVideoMetadata
import com.lawmobile.data.entities.VideoListMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.entities.VideoMetadata
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class FileListRepositoryImplTest {

    private val fileListRemoteDataSource: FileListRemoteDataSource = mockk()

    private val fileListRepositoryImpl: FileListRepositoryImpl by lazy {
        FileListRepositoryImpl(fileListRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun testSavePartnerIdVideosFlow() {
        val cameraConnectFile = CameraConnectFile("", "", "", "")
        val remoteVideoMetadata = RemoteVideoMetadata(CameraConnectVideoMetadata(""), false)
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns remoteVideoMetadata
        every { VideoListMetadata.saveOrUpdateVideoMetadata(any()) } returns Unit
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns Result.Success(Unit)

        runBlocking {
            fileListRepositoryImpl.savePartnerIdVideos(listOf(cameraConnectFile), "")
        }

        coVerify { fileListRemoteDataSource.savePartnerIdVideos(any()) }
    }

    @Test
    fun testSavePartnerIdVideosSuccess() {
        val result = Result.Success(Unit)
        val cameraConnectFile = CameraConnectFile("", "", "", "1")
        val remoteVideoMetadata = RemoteVideoMetadata(
            CameraConnectVideoMetadata("", "", "", "2", "", VideoMetadata(partnerID = "abc")),
            false
        )

        VideoListMetadata.metadataList = mutableListOf(remoteVideoMetadata)
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns result

        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.savePartnerIdVideos(
                    listOf(cameraConnectFile),
                    "1234"
                ), result
            )
            Assert.assertEquals(1, VideoListMetadata.metadataList.size)
        }

        val resultMetadata = CameraConnectVideoMetadata(
            "",
            "",
            "",
            "1",
            "",
            metadata = VideoMetadata(partnerID = "1234")
        )

        coVerify {
            delay(100)
            VideoListMetadata.getIndexVideoMetadata(any())
            fileListRemoteDataSource.savePartnerIdVideos(resultMetadata)
        }
    }

    @Test
    fun testSavePartnerIdVideosSuccessNull() {
        val result = Result.Success(Unit)
        val cameraConnectFile = CameraConnectFile("", "", "", "")
        VideoListMetadata.metadataList = mutableListOf()

        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns result

        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.savePartnerIdVideos(
                    listOf(cameraConnectFile),
                    "1234"
                ), result
            )
            Assert.assertEquals(1, VideoListMetadata.metadataList.size)
        }

        val resultMetadata = CameraConnectVideoMetadata(
            "",
            "",
            "",
            "",
            "",
            metadata = VideoMetadata(partnerID = "1234")
        )

        coVerify { fileListRemoteDataSource.savePartnerIdVideos(resultMetadata) }

    }

    @Test
    fun testSavePartnerIdVideosFailed() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)

        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns Result.Error(mockk())

        runBlocking {
            Assert.assertTrue(
                fileListRepositoryImpl.savePartnerIdVideos(
                    listOf(cameraConnectFile),
                    ""
                ) is Result.Error
            )
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)

        coEvery { fileListRemoteDataSource.savePartnerIdSnapshot(any()) } returns Result.Success(
            Unit
        )
        runBlocking {
            fileListRepositoryImpl.savePartnerIdSnapshot(listOf(cameraConnectFile), "")
        }
        coVerify { fileListRemoteDataSource.savePartnerIdSnapshot(any()) }
    }

    @Test
    fun testSavePartnerIdSnapshotSuccess() {
        val result = Result.Success(Unit)
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)

        coEvery { fileListRemoteDataSource.savePartnerIdSnapshot(any()) } returns result

        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.savePartnerIdSnapshot(
                    listOf(
                        cameraConnectFile
                    ), ""
                ), result
            )
        }

        coVerify {
            delay(100)
            fileListRemoteDataSource.savePartnerIdSnapshot(any())
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFailed() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)

        coEvery { fileListRemoteDataSource.savePartnerIdSnapshot(any()) } returns Result.Error(mockk())

        runBlocking {
            Assert.assertTrue(
                fileListRepositoryImpl.savePartnerIdSnapshot(
                    listOf(cameraConnectFile),
                    ""
                ) is Result.Error
            )
        }
    }
}