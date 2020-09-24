package com.lawmobile.data.repository.thumbnailList

import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.lawmobile.domain.entities.DomainInformationFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.commons.helpers.Result
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
        FileList.listOfImages = listOf(mockk(relaxed = true), mockk(relaxed = true))
        coEvery { thumbnailListRemoteDataSource.getImageBytes(any()) } returns Result.Success("Hola".toByteArray())
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        runBlocking {
            linkSnapshotsRepositoryImpl.getImageBytes(cameraConnectFile)
        }

        coVerify {
            thumbnailListRemoteDataSource.getImageBytes(any())
        }
    }

    @Test
    fun testGetImageByteListError() {
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val domainInformationFile = DomainInformationFile(cameraConnectFile)
        FileList.listOfImages = listOf(domainInformationFile, domainInformationFile)
        coEvery { thumbnailListRemoteDataSource.getImageBytes(any()) } returns Result.Error(mockk())

        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetImageByteListSuccess() {
        FileList.listOfImages = emptyList()
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true) {
            every { nameFolder } returns "200710009"
            every { name } returns "12345678"
        }
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile, cameraConnectFile)
        }
        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraResponse
        )
        coEvery { thumbnailListRemoteDataSource.getImageBytes(any()) } returns Result.Success("Hola".toByteArray())

        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }

    }

    @Test
    fun testGetImageListFlow() {
        FileList.listOfImages = listOf(mockk(), mockk())
        val cameraConnectFileResponseWithErrors = CameraConnectFileResponseWithErrors()
        cameraConnectFileResponseWithErrors.items.addAll(listOf(mockk(relaxed = true), mockk(relaxed = true)))
        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraConnectFileResponseWithErrors
        )
        runBlocking {
            linkSnapshotsRepositoryImpl.getSnapshotList()
        }

        coVerify { thumbnailListRemoteDataSource.getSnapshotList() }
    }

    @Test
    fun testGetImageListSuccessMoreItems() {
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val domainInformationFile = DomainInformationFile(cameraConnectFile)
        FileList.listOfImages = listOf(domainInformationFile, domainInformationFile)
        val cameraConnectFileResponseWithErrors = CameraConnectFileResponseWithErrors()
        cameraConnectFileResponseWithErrors.items.addAll(
            listOf(
                cameraConnectFile,
                cameraConnectFile,
                cameraConnectFile
            )
        )
        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraConnectFileResponseWithErrors
        )
        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getSnapshotList()
            Assert.assertEquals((result as Result.Success).data.listItems.size, 3)
        }
    }

    @Test
    fun testGetImageListSuccessMoreLessItems() {
        val cameraConnectFile =
            CameraConnectFile("1010202000", "10-10-2020 12:00:00", "", "1010202000")
        val domainInformationFile = DomainInformationFile(cameraConnectFile)
        FileList.listOfImages = listOf(
            domainInformationFile,
            domainInformationFile,
            domainInformationFile,
            domainInformationFile
        )
        val cameraConnectFileResponseWithErrors = CameraConnectFileResponseWithErrors()
        cameraConnectFileResponseWithErrors.items.addAll(
            listOf(
                cameraConnectFile,
                cameraConnectFile
            )
        )
        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraConnectFileResponseWithErrors
        )
        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getSnapshotList()
            Assert.assertEquals((result as Result.Success).data.listItems.size, 4)
        }
    }

    @Test
    fun testGetImageListError() {
        FileList.listOfImages = emptyList()
        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns Result.Error(Exception("Exception"))
        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getSnapshotList()
            Assert.assertTrue(result is Result.Error)
        }
    }
}