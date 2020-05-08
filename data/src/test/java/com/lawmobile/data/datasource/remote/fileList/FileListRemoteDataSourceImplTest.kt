package com.lawmobile.data.datasource.remote.fileList

import com.lawmobile.data.InstantExecutorExtension
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class FileListRemoteDataSourceImplTest {

    private val cameraConnectService: CameraConnectService = mockk()

    private val fileListRemoteDataSourceImpl by lazy {
        FileListRemoteDataSourceImpl(cameraConnectService)
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { cameraConnectService.getListOfImages() } returns Result.Success(mockk())
        runBlocking {
            fileListRemoteDataSourceImpl.getSnapshotList()
        }
        coVerify { cameraConnectService.getListOfImages() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val result = Result.Success(listOf(mockk<CameraConnectFile>()))
        coEvery { cameraConnectService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraConnectService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetVideoListFlow() {
        coEvery { cameraConnectService.getListOfVideos() } returns Result.Success(mockk())
        runBlocking {
            fileListRemoteDataSourceImpl.getVideoList()
        }
        coVerify { cameraConnectService.getListOfVideos() }
    }

    @Test
    fun testGetVideoListSuccess() {
        val result = Result.Success(listOf(mockk<CameraConnectFile>()))
        coEvery { cameraConnectService.getListOfVideos() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getVideoList(), result)
        }
    }

    @Test
    fun testGetVideoListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraConnectService.getListOfVideos() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getVideoList(), result)
        }
    }

}