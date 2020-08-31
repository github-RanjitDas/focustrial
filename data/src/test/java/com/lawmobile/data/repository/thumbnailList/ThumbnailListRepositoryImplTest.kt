package com.lawmobile.data.repository.thumbnailList

import com.lawmobile.data.datasource.remote.thumbnailList.ThumbnailListRemoteDataSource
import com.lawmobile.data.entities.FileList
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

        runBlocking {
            linkSnapshotsRepositoryImpl.getImageByteList(1)
        }

        coVerify {
            thumbnailListRemoteDataSource.getImageBytes(any())
        }
    }

    @Test
    fun testGetImageByteListSnapshotListError() {
        FileList.listOfImages = emptyList()
        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns Result.Error(mockk())

        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getImageByteList(1)
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetImageByteListError() {
        FileList.listOfImages = emptyList()
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
        }
        coEvery { thumbnailListRemoteDataSource.getSnapshotList() } returns Result.Success(
            cameraResponse
        )
        coEvery { thumbnailListRemoteDataSource.getImageBytes(any()) } returns Result.Error(mockk())

        runBlocking {
            val result = linkSnapshotsRepositoryImpl.getImageByteList(1)
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
            val result = linkSnapshotsRepositoryImpl.getImageByteList(1)
            Assert.assertTrue(result is Result.Success)
        }

    }

    @Test
    fun testGetImageListSizeValue() {
        FileList.listOfImages = listOf(mockk(), mockk())

        val result = linkSnapshotsRepositoryImpl.getImageListSize()
        Assert.assertEquals(2, result)
    }
}