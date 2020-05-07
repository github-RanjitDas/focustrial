package com.lawmobile.data.repository.fileList

import com.lawmobile.data.datasource.remote.fileList.FileListRemoteDataSource
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FileListRepositoryImplTest {

    private val fileListRemoteDataSource: FileListRemoteDataSource = mockk()

    private val fileListRepositoryImpl: FileListRepositoryImpl by lazy {
        FileListRepositoryImpl(fileListRemoteDataSource)
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns Result.Success(mockk())
        runBlocking {
            fileListRepositoryImpl.getSnapshotList()
        }
        coVerify { fileListRemoteDataSource.getSnapshotList() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val result = Result.Success(listOf(mockk<CameraConnectFile>()))
        coEvery { fileListRemoteDataSource.getSnapshotList() } returns result
        runBlocking {
            Assert.assertEquals(fileListRepositoryImpl.getSnapshotList(), result)
        }
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
        coEvery { fileListRemoteDataSource.getVideoList() } returns Result.Success(mockk())
        runBlocking {
            fileListRepositoryImpl.getVideoList()
        }
        coVerify { fileListRemoteDataSource.getVideoList() }
    }

    @Test
    fun testGetVideoListSuccess() {
        val result = Result.Success(listOf(mockk<CameraConnectFile>()))
        coEvery { fileListRemoteDataSource.getVideoList() } returns result
        runBlocking {
            Assert.assertEquals(fileListRepositoryImpl.getVideoList(), result)
        }
    }

    @Test
    fun testGetVideoListFailed() {
        val result = Result.Error(mockk())
        coEvery { fileListRemoteDataSource.getVideoList() } returns result
        runBlocking {
            Assert.assertEquals(fileListRepositoryImpl.getVideoList(), result)
        }
    }
}