package com.lawmobile.data.repository.simpleList

import com.lawmobile.data.datasource.remote.simpleList.SimpleListRemoteDataSource
import com.lawmobile.domain.entities.FileList
import com.lawmobile.domain.entities.VideoListMetadata
import com.lawmobile.data.mappers.FileMapper
import com.lawmobile.domain.entities.DomainInformationFile
import com.lawmobile.domain.entities.DomainInformationFileResponse
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
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
        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse: FileResponseWithErrors = mockk {
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
        val cameraFile: CameraFile = mockk(relaxed = true)
        val cameraResponse = FileResponseWithErrors().apply {
            items.addAll(mutableListOf(cameraFile))
        }
        val listDomain =
            mutableListOf(DomainInformationFile(FileMapper.cameraToDomain(cameraFile), null))
        val domainInformationFileResponse =
            DomainInformationFileResponse(listDomain, mutableListOf())

        coEvery { simpleListRemoteDataSource.getSnapshotList() } returns
                Result.Success(cameraResponse)
        FileList.imageList = emptyList()

        runBlocking {
            val result = simpleListRepositoryImpl.getSnapshotList() as Result.Success
            Assert.assertEquals(result.data.items, domainInformationFileResponse.items)
            Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
        }
    }

    @Test
    fun testGetSnapshotListSuccessWithLessValuesInDataSource() {
        val cameraFile: CameraFile = mockk(relaxed = true)
        val cameraResponse = FileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraFile, cameraFile))

        coEvery { simpleListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraResponse
        )

        FileList.imageList = listOf(
            DomainInformationFile(FileMapper.cameraToDomain(cameraFile), null),
            DomainInformationFile(FileMapper.cameraToDomain(cameraFile), null)
        )
        val imageList = FileList.imageList as MutableList
        val domainInformationFileResponse =
            DomainInformationFileResponse(imageList, mutableListOf())

        runBlocking {
            val result = simpleListRepositoryImpl.getSnapshotList() as Result.Success
            Assert.assertEquals(result.data.items, domainInformationFileResponse.items)
            Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
        }
    }

    @Test
    fun testGetSnapshotListSuccessWithEqualsValuesInDataSource() {
        mockkObject(FileList)
        every { FileList.changeImageList(any()) } just Runs

        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse = FileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile, cameraConnectFile))
        val listImages = listOf(
            DomainInformationFile(FileMapper.cameraToDomain(cameraConnectFile), null),
            DomainInformationFile(FileMapper.cameraToDomain(cameraConnectFile), null)
        )

        coEvery { simpleListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraResponse
        )
        FileList.imageList = listImages

        runBlocking {
            val result = simpleListRepositoryImpl.getSnapshotList() as Result.Success
            Assert.assertEquals(result.data.items, listImages)
        }

        coVerify { FileList.changeImageList(listImages) }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { simpleListRemoteDataSource.getSnapshotList() } returns result
        runBlocking { Assert.assertEquals(simpleListRepositoryImpl.getSnapshotList(), result) }
    }

    @Test
    fun testGetVideoListFlow() {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")
        val cameraResponse = FileResponseWithErrors()
        cameraResponse.items.add(cameraConnectFile)

        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        runBlocking { simpleListRepositoryImpl.getVideoList() }
        coVerify { simpleListRemoteDataSource.getVideoList() }
    }

    @Test
    fun testGetVideoListSuccess() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null

        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse = FileResponseWithErrors()
        cameraResponse.items.addAll(arrayListOf(cameraConnectFile))
        val listDomain = mutableListOf(
            DomainInformationFile(FileMapper.cameraToDomain(cameraConnectFile), null)
        )
        val domainInformationFileResponse =
            DomainInformationFileResponse(listDomain, mutableListOf())

        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        FileList.videoList = emptyList()

        runBlocking {
            val result = simpleListRepositoryImpl.getVideoList() as Result.Success
            Assert.assertEquals(result.data.items, domainInformationFileResponse.items)
            Assert.assertEquals(result.data.errors, result.data.errors)
        }
    }

    @Test
    fun testGetVideoListSuccessLessInResponseDataSource() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null

        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
            every { errors } returns arrayListOf("20201228/")
        }

        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)

        FileList.videoList = listOf(
            DomainInformationFile(FileMapper.cameraToDomain(cameraConnectFile), null)
        )
        val videoList = FileList.videoList as MutableList
        val domainInformationFileResponse =
            DomainInformationFileResponse(videoList, mutableListOf("20201228/"))

        runBlocking {
            val result = simpleListRepositoryImpl.getVideoList() as Result.Success
            Assert.assertEquals(result.data.items, domainInformationFileResponse.items)
            Assert.assertEquals(result.data.errors, domainInformationFileResponse.errors)
        }
    }

    @Test
    fun testGetVideoListSuccessEqualsInResponseDataSource() {
        mockkObject(VideoListMetadata)
        every { VideoListMetadata.getVideoMetadata(any()) } returns null
        mockkObject(FileList)
        every { FileList.changeVideoList(any()) } just Runs

        val cameraConnectFile: CameraFile = mockk(relaxed = true)
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile, cameraConnectFile)
            every { errors } returns arrayListOf()
        }

        coEvery { simpleListRemoteDataSource.getVideoList() } returns Result.Success(cameraResponse)
        val listOfVideos = listOf(
            DomainInformationFile(FileMapper.cameraToDomain(cameraConnectFile), null),
            DomainInformationFile(FileMapper.cameraToDomain(cameraConnectFile), null)
        )
        FileList.videoList = listOfVideos

        runBlocking {
            val result = simpleListRepositoryImpl.getVideoList() as Result.Success
            Assert.assertEquals(
                result.data.items,
                listOfVideos
            )
        }
        coVerify { FileList.changeVideoList(listOfVideos) }
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