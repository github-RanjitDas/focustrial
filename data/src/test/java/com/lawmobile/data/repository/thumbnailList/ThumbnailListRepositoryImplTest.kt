package com.lawmobile.data.repository.thumbnailList

import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSource
import com.lawmobile.domain.entities.FileList
import com.lawmobile.data.mappers.FileMapper
import com.lawmobile.domain.entities.DomainInformationFile
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ThumbnailListRepositoryImplTest {

    private val thumbnailListRemoteDataSource: ThumbnailListRemoteDataSource = mockk()

    private val linkSnapshotsRepositoryImpl: ThumbnailListRepositoryImpl by lazy {
        ThumbnailListRepositoryImpl(thumbnailListRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun testGetImageByteListFlow() {
        val cameraFile =
            CameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        FileList.imageList = listOf(mockk(relaxed = true), mockk(relaxed = true))

        coEvery { thumbnailListRemoteDataSource.getImageBytes(any()) } returns Result.Success("Hola".toByteArray())

        runBlocking {
            linkSnapshotsRepositoryImpl.getImageBytes(FileMapper.cameraToDomain(cameraFile))
        }

        coVerify {
            thumbnailListRemoteDataSource.getImageBytes(any())
        }
    }

    @Test
    fun testGetImageByteListError() {
        val cameraFile =
            CameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val domainInformationFile =
            DomainInformationFile(FileMapper.cameraToDomain(cameraFile))
        FileList.imageList = listOf(domainInformationFile, domainInformationFile)

        coEvery { thumbnailListRemoteDataSource.getImageBytes(any()) } returns
                Result.Error(mockk())

        runBlocking {
            val result = linkSnapshotsRepositoryImpl
                .getImageBytes(FileMapper.cameraToDomain(cameraFile))
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetImageByteListSuccess() {
        FileList.imageList = emptyList()
        val cameraFile: CameraFile = mockk(relaxed = true) {
            every { nameFolder } returns "200710009"
            every { name } returns "12345678"
        }
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraFile, cameraFile)
        }

        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns
                Result.Success(cameraResponse)
        coEvery { thumbnailListRemoteDataSource.getImageBytes(any()) } returns
                Result.Success("Hola".toByteArray())

        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getImageBytes(
                FileMapper.cameraToDomain(cameraFile)
            )
            Assert.assertTrue(result is Result.Success)
        }

    }

    @Test
    fun testGetImageListFlow() {
        FileList.imageList = listOf(mockk(), mockk())
        val cameraConnectFileResponseWithErrors = FileResponseWithErrors().apply {
            items.addAll(listOf(mockk(relaxed = true), mockk(relaxed = true)))
        }

        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns
                Result.Success(cameraConnectFileResponseWithErrors)

        runBlocking { linkSnapshotsRepositoryImpl.getSnapshotList() }

        coVerify { thumbnailListRemoteDataSource.getSnapshotList() }
    }

    @Test
    fun testGetImageListSuccessMoreItems() {
        val cameraFile =
            CameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val domainInformationFile =
            DomainInformationFile(FileMapper.cameraToDomain(cameraFile))
        FileList.imageList = listOf(domainInformationFile, domainInformationFile)
        val cameraConnectFileResponseWithErrors = FileResponseWithErrors().apply {
            items.addAll(listOf(cameraFile, cameraFile, cameraFile))
        }

        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns
                Result.Success(cameraConnectFileResponseWithErrors)

        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getSnapshotList()
            Assert.assertEquals((result as Result.Success).data.items.size, 3)
        }
    }

    @Test
    fun testGetImageListSuccessWithLessItemsFromCamera() {
        val cameraFile =
            CameraFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val domainInformationFile =
            DomainInformationFile(FileMapper.cameraToDomain(cameraFile))

        FileList.imageList = listOf(
            domainInformationFile,
            domainInformationFile,
            domainInformationFile,
            domainInformationFile
        )

        val cameraConnectFileResponseWithErrors = FileResponseWithErrors().apply {
            items.addAll(listOf(cameraFile, cameraFile))
        }

        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns
                Result.Success(cameraConnectFileResponseWithErrors)

        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getSnapshotList()
            Assert.assertEquals((result as Result.Success).data.items.size, 4)
        }
    }

    @Test
    fun testGetImageListError() {
        FileList.imageList = emptyList()
        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns Result.Error(Exception("Exception"))
        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getSnapshotList()
            Assert.assertTrue(result is Result.Error)
        }
    }
}