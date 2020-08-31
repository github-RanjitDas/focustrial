package com.lawmobile.data.datasource.remote.fileList

import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class FileListRemoteDataSourceImplTest {

    private val cameraConnectService: CameraConnectService = mockk()

    private val fileListRemoteDataSourceImpl by lazy {
        FileListRemoteDataSourceImpl(cameraConnectService)
    }

    @Test
    fun testSavePartnerIdVideosFlow() {
        coEvery { cameraConnectService.saveVideoMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            fileListRemoteDataSourceImpl.savePartnerIdVideos(mockk())
        }
        coVerify { cameraConnectService.saveVideoMetadata(any()) }
    }

    @Test
    fun testSavePartnerIdVideosSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraConnectService.saveVideoMetadata(any()) } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.savePartnerIdVideos(mockk()), result)
        }
    }

    @Test
    fun testSavePartnerIdVideosFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraConnectService.saveVideoMetadata(any()) } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.savePartnerIdVideos(mockk()), result)
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow() {
        coEvery { cameraConnectService.savePhotoMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            fileListRemoteDataSourceImpl.savePartnerIdSnapshot(mockk())
        }
        coVerify { cameraConnectService.savePhotoMetadata(any()) }
    }

    @Test
    fun testSavePartnerIdSnapshotsFlow() {
        coEvery { cameraConnectService.saveAllPhotoMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            fileListRemoteDataSourceImpl.savePartnerIdInAllSnapshots(mockk())
        }
        coVerify { cameraConnectService.saveAllPhotoMetadata(any()) }
    }

    @Test
    fun testSavePartnerIdSnapshotSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraConnectService.saveAllPhotoMetadata(any()) } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.savePartnerIdInAllSnapshots(mockk()), result)
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraConnectService.saveAllPhotoMetadata(any()) } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.savePartnerIdInAllSnapshots(mockk()), result)
        }
    }
}