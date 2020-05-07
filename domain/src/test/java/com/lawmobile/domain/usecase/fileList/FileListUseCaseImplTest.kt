package com.lawmobile.domain.usecase.fileList

import com.lawmobile.domain.repository.fileList.FileListRepository
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class FileListUseCaseImplTest {

    private val fileListRepository: FileListRepository = mockk()

    private val fileListUseCaseImpl: FileListUseCaseImpl by lazy {
        FileListUseCaseImpl(fileListRepository)
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { fileListRepository.getSnapshotList() } returns Result.Success(mockk())
        runBlocking {
            fileListUseCaseImpl.getSnapshotList()
        }
        coVerify { fileListRepository.getSnapshotList() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val result = Result.Success(listOf(mockk<CameraConnectFile>()))
        coEvery { fileListRepository.getSnapshotList() } returns result
        runBlocking {
            Assert.assertEquals(fileListUseCaseImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { fileListRepository.getSnapshotList() } returns result
        runBlocking {
            Assert.assertEquals(fileListUseCaseImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetVideoListFlow() {
        coEvery { fileListRepository.getVideoList() } returns Result.Success(mockk())
        runBlocking {
            fileListUseCaseImpl.getVideoList()
        }
        coVerify { fileListRepository.getVideoList() }
    }

    @Test
    fun testGetVideoListSuccess() {
        val result = Result.Success(listOf(mockk<CameraConnectFile>()))
        coEvery { fileListRepository.getVideoList() } returns result
        runBlocking {
            Assert.assertEquals(fileListUseCaseImpl.getVideoList(), result)
        }
    }

    @Test
    fun testGetVideoListFailed() {
        val result = Result.Error(mockk())
        coEvery { fileListRepository.getVideoList() } returns result
        runBlocking {
            Assert.assertEquals(fileListUseCaseImpl.getVideoList(), result)
        }
    }
}