package com.lawmobile.data.datasource.remote.thumbnailList

import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ThumbnailListRemoteDataSourceImplTest {

    private val cameraConnectService: CameraConnectService = mockk()
    private val linkSnapshotsRemoteDataSourceImpl: ThumbnailListRemoteDataSourceImpl by lazy {
        ThumbnailListRemoteDataSourceImpl(cameraConnectService)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun testGetImageBytesFlow() {
        val byteArray = "".toByteArray()
        coEvery { cameraConnectService.getImageBytes(any()) } returns Result.Success(byteArray)

        runBlocking {
            when (val result = linkSnapshotsRemoteDataSourceImpl.getImageBytes(mockk())) {
                is Result.Success -> Assert.assertEquals(byteArray, result.data)
            }
        }

        coVerify { cameraConnectService.getImageBytes(any()) }
    }

    @Test
    fun testGetImageBytesSuccess() {
        coEvery { cameraConnectService.getImageBytes(any()) } returns Result.Success("".toByteArray())

        runBlocking {
            val result = linkSnapshotsRemoteDataSourceImpl.getImageBytes(mockk())
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testGetImageBytesError() {
        coEvery { cameraConnectService.getImageBytes(any()) } returns Result.Error(mockk())

        runBlocking {
            val result = linkSnapshotsRemoteDataSourceImpl.getImageBytes(mockk())
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { cameraConnectService.getListOfImages() } returns Result.Success(mockk())
        runBlocking {
            linkSnapshotsRemoteDataSourceImpl.getSnapshotList()
        }
        coVerify { cameraConnectService.getListOfImages() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
        }
        val result = Result.Success(cameraResponse)
        coEvery { cameraConnectService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(linkSnapshotsRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraConnectService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(linkSnapshotsRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }
}