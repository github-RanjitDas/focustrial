package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.entities.RemoteVideoMetadata
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.domain.entity.DomainInformationFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectVideoMetadata
import com.safefleet.mobile.avml.cameras.entities.VideoMetadata
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FileListRepositoryImplTest {

    private val fileListRemoteDataSource: FileListRemoteDataSource = mockk()

    private val fileListRepositoryImpl: FileListRepositoryImpl by lazy {
        FileListRepositoryImpl(fileListRemoteDataSource)
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns Result.Success(listOf(mockk()))
        runBlocking {
            fileListRepositoryImpl.getSnapshotList()
        }
        coVerify { fileListRemoteDataSource.getSnapshotList() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns Result.Success(
            listOf(
                cameraConnectFile
            )
        )
        val response = Result.Success(listOf(DomainInformationFile(cameraConnectFile, null)))
        runBlocking {
            Assert.assertEquals(fileListRepositoryImpl.getSnapshotList(), response)
        }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns result
        runBlocking {
            Assert.assertEquals(fileListRepositoryImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetVideoListFlow() {
        coEvery { fileListRemoteDataSource.getVideoList() } returns Result.Success(
            listOf(
                mockk(
                    relaxed = true
                )
            )
        )
        runBlocking {
            fileListRepositoryImpl.getVideoList()
        }
        coVerify { fileListRemoteDataSource.getVideoList() }
    }

    @Test
    fun testGetVideoListSuccess() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        coEvery { fileListRemoteDataSource.getVideoList() } returns Result.Success(
            listOf(
                cameraConnectFile
            )
        )
        val response = Result.Success(listOf(DomainInformationFile(cameraConnectFile, null)))
        runBlocking {
            Assert.assertEquals(fileListRepositoryImpl.getVideoList(), response)
        }
    }

    @Test
    fun testGetVideoListFailed() {
        val result = Result.Error(mockk())
        coEvery { fileListRemoteDataSource.getVideoList() } returns result
        runBlocking {
            Assert.assertEquals(fileListRepositoryImpl.getVideoList(), result)
        }
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
        val cameraConnectFile = CameraConnectFile("", "", "", "")
        val cameraConnectVideoMetadata = RemoteVideoMetadata(
            CameraConnectVideoMetadata("", "", "", "", "", VideoMetadata(partnerID = "abc")),
            false
        )

        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns cameraConnectVideoMetadata
        every { VideoListMetadata.saveOrUpdateVideoMetadata(any()) } returns Unit
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns result

        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.savePartnerIdVideos(
                    listOf(cameraConnectFile),
                    "1234"
                ), result
            )
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
    fun testSavePartnerIdVideosSuccessNull() {
        val result = Result.Success(Unit)
        val cameraConnectFile = CameraConnectFile("", "", "", "")

        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        every { VideoListMetadata.saveOrUpdateVideoMetadata(any()) } returns Unit
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns result

        runBlocking {
            Assert.assertEquals(
                fileListRepositoryImpl.savePartnerIdVideos(
                    listOf(cameraConnectFile),
                    "1234"
                ), result
            )
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