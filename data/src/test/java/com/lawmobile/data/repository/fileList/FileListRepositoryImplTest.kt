package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.entities.RemoteVideoMetadata
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainVideoMetadata
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
        val domainCameraFile = DomainCameraFile("", "", "", "")
        val remoteVideoMetadata = RemoteVideoMetadata(DomainVideoMetadata(""), false)
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns remoteVideoMetadata
        every { VideoListMetadata.saveOrUpdateVideoMetadata(any()) } returns Unit
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns Result.Success(Unit)

        runBlocking {
            fileListRepositoryImpl.savePartnerIdVideos(listOf(domainCameraFile), "")
        }

        coVerify { fileListRemoteDataSource.savePartnerIdVideos(any()) }
    }

    @Test
    fun testSavePartnerIdVideosSuccess() {
        val result = Result.Success(Unit)
        val domainCameraFile = DomainCameraFile("", "", "", "1")
        val domainVideoMetadata = DomainVideoMetadata(
            fileName = "",
            officerId = "",
            path = "",
            nameFolder = "2",
            serialNumber = "",
            metadata = DomainMetadata(partnerID = "abc")
        )
        val remoteVideoMetadata = RemoteVideoMetadata(domainVideoMetadata, false)

        VideoListMetadata.metadataList = mutableListOf(remoteVideoMetadata)
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns result

        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.savePartnerIdVideos(listOf(domainCameraFile), "1234"),
                result
            )
            Assert.assertEquals(1, VideoListMetadata.metadataList.size)
        }

        coVerify {
            delay(100)
            VideoListMetadata.getIndexVideoMetadata(domainCameraFile.name)
            fileListRemoteDataSource.savePartnerIdVideos(any())
        }
    }

    @Test
    fun testSavePartnerIdVideosSuccessNull() {
        val result = Result.Success(Unit)
        val domainCameraFile = DomainCameraFile("", "", "", "")
        VideoListMetadata.metadataList = mutableListOf()

        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns result

        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.savePartnerIdVideos(
                    listOf(domainCameraFile),
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
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns Result.Error(mockk())

        runBlocking {
            Assert.assertTrue(
                fileListRepositoryImpl.savePartnerIdVideos(
                    listOf(domainCameraFile),
                    ""
                ) is Result.Error
            )
        }
    }

    @Test
    fun testSavePartnerIdSnapshotsFlow() {
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        coEvery { fileListRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns Result.Success(
            Unit
        )
        coEvery { fileListRemoteDataSource.getSavedPhotosMetadata() } returns Result.Success(
            emptyList()
        )
        coEvery { fileListRemoteDataSource.savePartnerIdSnapshot(any()) } returns Result.Success(
            Unit
        )
        runBlocking {
            fileListRepositoryImpl.savePartnerIdSnapshot(listOf(domainCameraFile), "")
        }
        coVerify { fileListRemoteDataSource.savePartnerIdInAllSnapshots(any()) }
    }

    @Test
    fun testSavePartnerIdSnapshotSuccess() {
        val result = Result.Success(Unit)
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        coEvery { fileListRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns result
        coEvery { fileListRemoteDataSource.savePartnerIdSnapshot(any()) } returns Result.Success(
            Unit
        )
        coEvery { fileListRemoteDataSource.getSavedPhotosMetadata() } returns Result.Success(
            emptyList()
        )
        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.savePartnerIdSnapshot(
                    listOf(
                        domainCameraFile
                    ), ""
                ), result
            )
        }

        coVerify {
            delay(100)
            fileListRemoteDataSource.savePartnerIdInAllSnapshots(any())
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFailed() {
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        coEvery { fileListRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns Result.Error(
            mockk()
        )
        coEvery { fileListRemoteDataSource.savePartnerIdSnapshot(any()) } returns Result.Success(
            Unit
        )
        coEvery { fileListRemoteDataSource.getSavedPhotosMetadata() } returns Result.Success(
            emptyList()
        )
        runBlocking {
            Assert.assertTrue(
                fileListRepositoryImpl.savePartnerIdSnapshot(
                    listOf(domainCameraFile),
                    ""
                ) is Result.Error
            )
        }
    }
}