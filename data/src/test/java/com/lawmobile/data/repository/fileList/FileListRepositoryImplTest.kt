package com.lawmobile.data.repository.fileList

import com.lawmobile.body_cameras.entities.VideoInformation
import com.lawmobile.body_cameras.entities.VideoMetadata
import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainMetadata
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.entities.RemoteVideoMetadata
import com.lawmobile.domain.entities.VideoListMetadata
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

@ExperimentalCoroutinesApi
internal class FileListRepositoryImplTest {

    private val fileListRemoteDataSource: FileListRemoteDataSource = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val fileListRepositoryImpl: FileListRepositoryImpl by lazy {
        FileListRepositoryImpl(fileListRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
        clearAllMocks()
    }

    @Test
    fun testSavePartnerIdVideosFlow() = dispatcher.runBlockingTest {
        val domainCameraFile = DomainCameraFile("", "", "", "")
        val remoteVideoMetadata = RemoteVideoMetadata(DomainVideoMetadata(""), false)
        mockkObject(VideoListMetadata)

        every { VideoListMetadata.getVideoMetadata(any()) } returns remoteVideoMetadata
        every { VideoListMetadata.saveOrUpdateVideoMetadata(any()) } returns Unit
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns Result.Success(Unit)

        fileListRepositoryImpl.savePartnerIdVideos(listOf(domainCameraFile), "")

        coVerify { fileListRemoteDataSource.savePartnerIdVideos(any()) }
    }

    @Test
    fun testSavePartnerIdVideosSuccess() = dispatcher.runBlockingTest {
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

        Assert.assertEquals(
            fileListRepositoryImpl.savePartnerIdVideos(listOf(domainCameraFile), "1234"),
            result
        )
        Assert.assertEquals(1, VideoListMetadata.metadataList.size)

        coVerify {
            VideoListMetadata.getIndexVideoMetadata(domainCameraFile.name)
            fileListRemoteDataSource.savePartnerIdVideos(any())
        }
    }

    @Test
    fun testSavePartnerIdVideosSuccessNull() = dispatcher.runBlockingTest {
        val result = Result.Success(Unit)
        val domainCameraFile = DomainCameraFile("", "", "", "")
        VideoListMetadata.metadataList = mutableListOf()

        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns result

        Assert.assertEquals(
            fileListRepositoryImpl.savePartnerIdVideos(
                listOf(domainCameraFile),
                "1234"
            ),
            result
        )
        Assert.assertEquals(1, VideoListMetadata.metadataList.size)

        val resultMetadata = VideoInformation(
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
    fun testSavePartnerIdVideosFailed() = dispatcher.runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        coEvery { fileListRemoteDataSource.savePartnerIdVideos(any()) } returns Result.Error(mockk())

        val result = fileListRepositoryImpl.savePartnerIdVideos(listOf(domainCameraFile), "")
        Assert.assertTrue(result is Result.Error)
    }

    @Test
    fun testSavePartnerIdSnapshotsFlow() = dispatcher.runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        coEvery {
            fileListRemoteDataSource.savePartnerIdInAllSnapshots(any())
        } returns Result.Success(Unit)

        coEvery {
            fileListRemoteDataSource.getSavedPhotosMetadata()
        } returns Result.Success(emptyList())

        coEvery {
            fileListRemoteDataSource.savePartnerIdSnapshot(any())
        } returns Result.Success(Unit)

        fileListRepositoryImpl.savePartnerIdSnapshot(listOf(domainCameraFile), "")

        coVerify { fileListRemoteDataSource.savePartnerIdInAllSnapshots(any()) }
    }

    @Test
    fun testSavePartnerIdSnapshotSuccess() = dispatcher.runBlockingTest {
        val result = Result.Success(Unit)
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        coEvery { fileListRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns result

        coEvery {
            fileListRemoteDataSource.savePartnerIdSnapshot(any())
        } returns Result.Success(Unit)

        coEvery {
            fileListRemoteDataSource.getSavedPhotosMetadata()
        } returns Result.Success(emptyList())

        Assert.assertEquals(
            fileListRepositoryImpl.savePartnerIdSnapshot(listOf(domainCameraFile), ""),
            result
        )

        coVerify {
            fileListRemoteDataSource.savePartnerIdInAllSnapshots(any())
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFailed() = dispatcher.runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        coEvery {
            fileListRemoteDataSource.savePartnerIdInAllSnapshots(any())
        } returns Result.Error(mockk())

        coEvery {
            fileListRemoteDataSource.savePartnerIdSnapshot(any())
        } returns Result.Success(Unit)

        coEvery {
            fileListRemoteDataSource.getSavedPhotosMetadata()
        } returns Result.Success(emptyList())

        val result = fileListRepositoryImpl.savePartnerIdSnapshot(listOf(domainCameraFile), "")
        Assert.assertTrue(result is Result.Error)
    }

    @Test
    fun testSavePartnerIdAudiosFlow() = dispatcher.runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        coEvery {
            fileListRemoteDataSource.savePartnerIdAudios(any())
        } returns Result.Success(Unit)

        fileListRepositoryImpl.savePartnerIdAudios(listOf(domainCameraFile), "")

        coVerify { fileListRemoteDataSource.savePartnerIdAudios(any()) }
    }

    @Test
    fun testSavePartnerIdAudiosSuccess() = dispatcher.runBlockingTest {
        val result = Result.Success(Unit)
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        coEvery {
            fileListRemoteDataSource.savePartnerIdAudios(any())
        } returns Result.Success(Unit)

        Assert.assertEquals(
            fileListRepositoryImpl.savePartnerIdAudios(listOf(domainCameraFile), ""),
            result
        )
    }

    @Test
    fun testSavePartnerIdAudiosFailed() = dispatcher.runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk(relaxed = true)

        coEvery {
            fileListRemoteDataSource.savePartnerIdAudios(any())
        } returns Result.Error(Exception())

        val result = fileListRepositoryImpl.savePartnerIdAudios(listOf(domainCameraFile), "")
        Assert.assertTrue(result is Result.Error)
    }
}
