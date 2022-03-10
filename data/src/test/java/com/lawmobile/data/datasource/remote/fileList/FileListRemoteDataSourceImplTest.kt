package com.lawmobile.data.datasource.remote.fileList

import com.lawmobile.body_cameras.CameraService
import com.lawmobile.body_cameras.entities.PhotoInformation
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class FileListRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }

    private val fileListRemoteDataSourceImpl by lazy {
        FileListRemoteDataSourceImpl(cameraServiceFactory)
    }

    @Test
    fun testSavePartnerIdVideosFlow() {
        coEvery { cameraService.saveVideoMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            fileListRemoteDataSourceImpl.savePartnerIdVideos(mockk())
        }
        coVerify { cameraService.saveVideoMetadata(any()) }
    }

    @Test
    fun testSavePartnerIdVideosSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraService.saveVideoMetadata(any()) } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.savePartnerIdVideos(mockk()), result)
        }
    }

    @Test
    fun testSavePartnerIdVideosFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraService.saveVideoMetadata(any()) } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.savePartnerIdVideos(mockk()), result)
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow() {
        coEvery { cameraService.savePhotoMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            fileListRemoteDataSourceImpl.savePartnerIdSnapshot(mockk())
        }
        coVerify { cameraService.savePhotoMetadata(any()) }
    }

    @Test
    fun testSavePartnerIdSnapshotSuccess() {
        coEvery { cameraService.savePhotoMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            val result = fileListRemoteDataSourceImpl.savePartnerIdSnapshot(mockk())
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testSavePartnerIdSnapshotError() {
        coEvery { cameraService.savePhotoMetadata(any()) } returns Result.Error(Exception())
        runBlocking {
            val result = fileListRemoteDataSourceImpl.savePartnerIdSnapshot(mockk())
            Assert.assertTrue(result is Result.Error)
        }
    }

    @Test
    fun testGetSavedPhotosMetadataFlow() {
        coEvery { cameraService.getMetadataOfPhotos() } returns Result.Success(mockk(relaxed = true))
        runBlocking {
            fileListRemoteDataSourceImpl.getSavedPhotosMetadata()
        }
        coVerify { cameraService.getMetadataOfPhotos() }
    }

    @Test
    fun testGetSavedPhotosMetadataSuccess() {
        val photoInformation: PhotoInformation = mockk(relaxed = true)
        val result = Result.Success(listOf(photoInformation))
        coEvery { cameraService.getMetadataOfPhotos() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getSavedPhotosMetadata(), result)
        }
    }

    @Test
    fun testGetSavedPhotosMetadataFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraService.getMetadataOfPhotos() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getSavedPhotosMetadata(), result)
        }
    }

    @Test
    fun testSavePartnerIdSnapshotsFlow() {
        coEvery { cameraService.saveAllPhotoMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            fileListRemoteDataSourceImpl.savePartnerIdInAllSnapshots(mockk())
        }
        coVerify { cameraService.saveAllPhotoMetadata(any()) }
    }

    @Test
    fun testSavePartnerIdAllSnapshotsSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraService.saveAllPhotoMetadata(any()) } returns result
        runBlocking {
            Assert.assertEquals(
                result,
                fileListRemoteDataSourceImpl.savePartnerIdInAllSnapshots(mockk())
            )
        }
    }

    @Test
    fun testSavePartnerIdAllSnapshotsFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraService.saveAllPhotoMetadata(any()) } returns result
        runBlocking {
            Assert.assertEquals(
                result,
                fileListRemoteDataSourceImpl.savePartnerIdInAllSnapshots(mockk())
            )
        }
    }

    @Test
    fun testSavePartnerIdAudiosFlow() {
        coEvery { cameraService.saveAudioMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            fileListRemoteDataSourceImpl.savePartnerIdAudios(mockk())
        }
        coVerify { cameraService.saveAudioMetadata(any()) }
    }

    @Test
    fun testSavePartnerIdAudiosSuccess() {
        coEvery { cameraService.saveAudioMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            val result = fileListRemoteDataSourceImpl.savePartnerIdAudios(mockk())
            Assert.assertTrue(result is Result.Success)
        }
    }

    @Test
    fun testSavePartnerIdAudiosError() {
        coEvery { cameraService.saveAudioMetadata(any()) } returns Result.Error(Exception())
        runBlocking {
            val result = fileListRemoteDataSourceImpl.savePartnerIdAudios(mockk())
            Assert.assertTrue(result is Result.Error)
        }
    }
}
