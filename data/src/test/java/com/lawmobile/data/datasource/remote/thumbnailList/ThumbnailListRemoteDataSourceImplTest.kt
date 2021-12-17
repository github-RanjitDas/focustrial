package com.lawmobile.data.datasource.remote.thumbnailList

import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class ThumbnailListRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }
    private val linkSnapshotsRemoteDataSourceImpl: ThumbnailListRemoteDataSourceImpl by lazy {
        ThumbnailListRemoteDataSourceImpl(cameraServiceFactory)
    }

    @BeforeEach
    fun setup() {
        clearAllMocks()
        every { cameraServiceFactory.create() } returns cameraService
    }

    @Test
    fun testGetImageBytesFlow() {
        val byteArray = "".toByteArray()
        coEvery { cameraService.getImageBytes(any()) } returns Result.Success(byteArray)
        runBlocking {
            val result = linkSnapshotsRemoteDataSourceImpl.getImageBytes(mockk()) as Result.Success
            Assert.assertEquals(byteArray, result.data)
        }
        coVerify { cameraService.getImageBytes(any()) }
    }

    @Test
    fun testGetImageBytesSuccess() {
        coEvery { cameraService.getImageBytes(any()) } returns Result.Success("".toByteArray())
        runBlocking {
            val result = linkSnapshotsRemoteDataSourceImpl.getImageBytes(mockk())
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testGetImageBytesError() {
        coEvery { cameraService.getImageBytes(any()) } returns Result.Error(mockk())
        runBlocking {
            val result = linkSnapshotsRemoteDataSourceImpl.getImageBytes(mockk())
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { cameraService.getListOfImages() } returns Result.Success(mockk())
        runBlocking {
            linkSnapshotsRemoteDataSourceImpl.getSnapshotList()
        }
        coVerify { cameraService.getListOfImages() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val cameraFile: CameraFile = mockk(relaxed = true)
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraFile)
        }
        val result = Result.Success(cameraResponse)
        coEvery { cameraService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(linkSnapshotsRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(linkSnapshotsRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }
}
