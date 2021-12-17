package com.lawmobile.domain.usecase.fileList

import com.lawmobile.domain.repository.fileList.FileListRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
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
    fun testSavePartnerIdVideosFlow() {
        coEvery { fileListRepository.savePartnerIdVideos(any(), any()) } returns Result.Success(
            mockk()
        )
        runBlocking { fileListUseCaseImpl.savePartnerIdVideos(listOf(mockk()), "") }
        coVerify { fileListRepository.savePartnerIdVideos(any(), any()) }
    }

    @Test
    fun testSavePartnerIdVideosSuccess() {
        val result = Result.Success(Unit)
        coEvery { fileListRepository.savePartnerIdVideos(any(), any()) } returns result
        runBlocking {
            Assert.assertEquals(
                fileListUseCaseImpl.savePartnerIdVideos(listOf(mockk()), ""),
                result
            )
        }
    }

    @Test
    fun testSavePartnerIdVideosFailed() {
        val result = Result.Error(mockk())
        coEvery { fileListRepository.savePartnerIdVideos(any(), any()) } returns result
        runBlocking {
            Assert.assertEquals(
                fileListUseCaseImpl.savePartnerIdVideos(listOf(mockk()), ""),
                result
            )
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow() {
        coEvery { fileListRepository.savePartnerIdSnapshot(any(), any()) } returns Result.Success(
            mockk()
        )
        runBlocking { fileListUseCaseImpl.savePartnerIdSnapshot(listOf(mockk()), "") }
        coVerify { fileListRepository.savePartnerIdSnapshot(any(), any()) }
    }

    @Test
    fun testSavePartnerIdSnapshotSuccess() {
        val result = Result.Success(Unit)
        coEvery { fileListRepository.savePartnerIdSnapshot(any(), any()) } returns result
        runBlocking {
            Assert.assertEquals(
                fileListUseCaseImpl.savePartnerIdSnapshot(listOf(mockk()), ""),
                result
            )
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFailed() {
        val result = Result.Error(mockk())
        coEvery { fileListRepository.savePartnerIdSnapshot(any(), any()) } returns result
        runBlocking {
            Assert.assertEquals(
                fileListUseCaseImpl.savePartnerIdSnapshot(listOf(mockk()), ""),
                result
            )
        }
    }

    @Test
    fun testSavePartnerIdAudiosFlow() {
        coEvery { fileListRepository.savePartnerIdAudios(any(), any()) } returns Result.Success(
            mockk()
        )
        runBlocking { fileListUseCaseImpl.savePartnerIdAudios(listOf(mockk()), "") }
        coVerify { fileListRepository.savePartnerIdAudios(any(), any()) }
    }

    @Test
    fun testSavePartnerIdAudiosSuccess() {
        val result = Result.Success(Unit)
        coEvery { fileListRepository.savePartnerIdAudios(any(), any()) } returns result
        runBlocking {
            Assert.assertEquals(
                fileListUseCaseImpl.savePartnerIdAudios(listOf(mockk()), ""),
                result
            )
        }
    }

    @Test
    fun testSavePartnerIdAudiosFailed() {
        val result = Result.Error(mockk())
        coEvery { fileListRepository.savePartnerIdAudios(any(), any()) } returns result
        runBlocking {
            Assert.assertEquals(
                fileListUseCaseImpl.savePartnerIdAudios(listOf(mockk()), ""),
                result
            )
        }
    }
}
