package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.data.entities.RemoteVideoMetadata
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.safefleet.mobile.avml.cameras.entities.*
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    fun testGetSnapshotListFlow() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk{
            every { items } returns  arrayListOf(cameraConnectFile)
            every { errors } returns arrayListOf()
        }
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns Result.Success(cameraResponse)
        runBlocking {
            fileListRepositoryImpl.getSnapshotList()
        }
        coVerify { fileListRemoteDataSource.getSnapshotList() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        val cameraResponse = CameraConnectFileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile))

        coEvery { fileListRemoteDataSource.getSnapshotList() } returns Result.Success(cameraResponse)
        FileList.listOfImages = emptyList()

        val listDomain = listOf(DomainInformationFile(cameraConnectFile, null))
        val domainInformationFileResponse = DomainInformationFileResponse()
        domainInformationFileResponse.listItems.addAll(listDomain)
        runBlocking {
            val result = fileListRepositoryImpl.getSnapshotList() as Result.Success
            Assert.assertEquals(result.data.listItems, domainInformationFileResponse.listItems)
            Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
        }
    }


    @Test
    fun testGetSnapshotListSuccessWithLessValuesInDataSource() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse = CameraConnectFileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile,cameraConnectFile))
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns Result.Success(cameraResponse)
        FileList.listOfImages = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        val domainInformationFileResponse = DomainInformationFileResponse()
        domainInformationFileResponse.listItems.addAll(FileList.listOfImages)
        runBlocking {
            val result = fileListRepositoryImpl.getSnapshotList() as Result.Success
            Assert.assertEquals(result.data.listItems, domainInformationFileResponse.listItems)
            Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
        }
    }

    @Test
    fun testGetSnapshotListSuccessWithEqualsValuesInDataSource() {
        mockkObject(FileList)
        every { FileList.changeListOfImages(any()) } just Runs

        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse = CameraConnectFileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile,cameraConnectFile))
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns Result.Success(cameraResponse)
        val listImages = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        FileList.listOfImages = listImages
        runBlocking {
            val result = fileListRepositoryImpl.getSnapshotList() as Result.Success
            Assert.assertEquals(result.data.listItems, listImages)
        }

        coVerify { FileList.changeListOfImages(listImages) }
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
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk{
            every { items } returns  arrayListOf(cameraConnectFile)
            every { errors } returns arrayListOf()
        }
        coEvery { fileListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
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
        val cameraResponse = CameraConnectFileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile))
        coEvery { fileListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        FileList.listOfVideos = emptyList()
        val listDomain = listOf(DomainInformationFile(cameraConnectFile, null))
        val domainInformationFileResponse = DomainInformationFileResponse()
        domainInformationFileResponse.listItems.addAll(listDomain)
        runBlocking {
            val result = fileListRepositoryImpl.getVideoList() as Result.Success

            Assert.assertEquals(result.data.listItems, domainInformationFileResponse.listItems)
            Assert.assertEquals(result.data.errors, result.data.errors)
        }
    }

    @Test
    fun testGetVideoListSuccessLessInResponseDataSource() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk{
            every { items } returns  arrayListOf(cameraConnectFile)
            every { errors } returns arrayListOf("20201228/")
        }
        coEvery { fileListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        FileList.listOfVideos = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        val domainInformationFileResponse = DomainInformationFileResponse()
        domainInformationFileResponse.listItems.addAll(FileList.listOfVideos)
        domainInformationFileResponse.errors.addAll(arrayListOf("20201228/"))
        runBlocking {
            val result = fileListRepositoryImpl.getVideoList() as Result.Success
            Assert.assertEquals(result.data.listItems, domainInformationFileResponse.listItems)
            Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
        }
    }

    @Test
    fun testGetVideoListSuccessEqualsInResponseDataSource() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        mockkObject(FileList)
        every { FileList.changeListOfVideos(any()) } just Runs
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk{
            every { items } returns  arrayListOf(cameraConnectFile, cameraConnectFile)
            every { errors } returns arrayListOf()
        }
        coEvery { fileListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        val listOfVideos = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        FileList.listOfVideos = listOfVideos
        runBlocking {
            val result = fileListRepositoryImpl.getVideoList() as Result.Success
            Assert.assertEquals(
                result.data.listItems,
                listOfVideos
            )
        }

        coVerify { FileList.changeListOfVideos(listOfVideos) }
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