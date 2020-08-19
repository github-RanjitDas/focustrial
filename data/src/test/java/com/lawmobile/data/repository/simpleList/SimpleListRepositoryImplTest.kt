package com.lawmobile.data.repository.simpleList

import com.lawmobile.data.datasource.remote.simpleList.SimpleListRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.data.entities.VideoListMetadata
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SimpleListRepositoryImplTest {
    private val simpleListRemoteDataSource: SimpleListRemoteDataSource = mockk()

    private val simpleListRepositoryImpl: SimpleListRepositoryImpl by lazy {
        SimpleListRepositoryImpl(simpleListRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun testGetSnapshotListFlow() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
            every { errors } returns arrayListOf()
        }
        coEvery { simpleListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraResponse
        )
        runBlocking {
            simpleListRepositoryImpl.getSnapshotList()
        }
        coVerify { simpleListRemoteDataSource.getSnapshotList() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        val cameraResponse = CameraConnectFileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile))

        coEvery { simpleListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraResponse
        )
        FileList.listOfImages = emptyList()

        val listDomain = listOf(DomainInformationFile(cameraConnectFile, null))
        val domainInformationFileResponse = DomainInformationFileResponse()
        domainInformationFileResponse.listItems.addAll(listDomain)
        runBlocking {
            val result = simpleListRepositoryImpl.getSnapshotList() as Result.Success
            Assert.assertEquals(result.data.listItems, domainInformationFileResponse.listItems)
            Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
        }
    }

    @Test
    fun testGetSnapshotListSuccessWithLessValuesInDataSource() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse = CameraConnectFileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile, cameraConnectFile))
        coEvery { simpleListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraResponse
        )
        FileList.listOfImages = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        val domainInformationFileResponse = DomainInformationFileResponse()
        domainInformationFileResponse.listItems.addAll(FileList.listOfImages)
        runBlocking {
            val result = simpleListRepositoryImpl.getSnapshotList() as Result.Success
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
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile, cameraConnectFile))
        coEvery { simpleListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraResponse
        )
        val listImages = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        FileList.listOfImages = listImages
        runBlocking {
            val result = simpleListRepositoryImpl.getSnapshotList() as Result.Success
            Assert.assertEquals(result.data.listItems, listImages)
        }

        coVerify { FileList.changeListOfImages(listImages) }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { simpleListRemoteDataSource.getSnapshotList() } returns result
        runBlocking {
            Assert.assertEquals(simpleListRepositoryImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetVideoListFlow() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
            every { errors } returns arrayListOf()
        }
        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        runBlocking {
            simpleListRepositoryImpl.getVideoList()
        }
        coVerify { simpleListRemoteDataSource.getVideoList() }
    }

    @Test
    fun testGetVideoListSuccess() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse = CameraConnectFileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile))
        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        FileList.listOfVideos = emptyList()
        val listDomain = listOf(DomainInformationFile(cameraConnectFile, null))
        val domainInformationFileResponse = DomainInformationFileResponse()
        domainInformationFileResponse.listItems.addAll(listDomain)
        runBlocking {
            val result = simpleListRepositoryImpl.getVideoList() as Result.Success

            Assert.assertEquals(result.data.listItems, domainInformationFileResponse.listItems)
            Assert.assertEquals(result.data.errors, result.data.errors)
        }
    }

    @Test
    fun testGetVideoListSuccessLessInResponseDataSource() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
            every { errors } returns arrayListOf("20201228/")
        }
        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        FileList.listOfVideos = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        val domainInformationFileResponse = DomainInformationFileResponse()
        domainInformationFileResponse.listItems.addAll(FileList.listOfVideos)
        domainInformationFileResponse.errors.addAll(arrayListOf("20201228/"))
        runBlocking {
            val result = simpleListRepositoryImpl.getVideoList() as Result.Success
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
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile, cameraConnectFile)
            every { errors } returns arrayListOf()
        }
        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        val listOfVideos = listOf(
            DomainInformationFile(cameraConnectFile, null),
            DomainInformationFile(cameraConnectFile, null)
        )
        FileList.listOfVideos = listOfVideos
        runBlocking {
            val result = simpleListRepositoryImpl.getVideoList() as Result.Success
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
        coEvery { simpleListRemoteDataSource.getVideoList() } returns result
        runBlocking {
            Assert.assertEquals(simpleListRepositoryImpl.getVideoList(), result)
        }
    }
}