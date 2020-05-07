package com.lawmobile.data.datasource.remote.fileList

import com.lawmobile.data.InstantExecutorExtension
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.x1.CameraDataSource
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

    private val cameraDataSource: CameraDataSource = mockk()

    private val fileListRemoteDataSourceImpl by lazy {
        FileListRemoteDataSourceImpl(cameraDataSource)
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { cameraDataSource.getListOfImages() } returns Result.Success(mockk())
        runBlocking {
            fileListRemoteDataSourceImpl.getSnapshotList()
        }
        coVerify { cameraDataSource.getListOfImages() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val result = Result.Success(listOf(mockk<CameraConnectFile>()))
        coEvery { cameraDataSource.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraDataSource.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetVideoListFlow() {
        coEvery { cameraDataSource.getListOfVideos() } returns Result.Success(mockk())
        runBlocking {
            fileListRemoteDataSourceImpl.getVideoList()
        }
        coVerify { cameraDataSource.getListOfVideos() }
    }

    @Test
    fun testGetVideoListSuccess() {
        val result = Result.Success(listOf(mockk<CameraConnectFile>()))
        coEvery { cameraDataSource.getListOfVideos() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getVideoList(), result)
        }
    }

    @Test
    fun testGetVideoListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraDataSource.getListOfVideos() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getVideoList(), result)
        }
    }

}