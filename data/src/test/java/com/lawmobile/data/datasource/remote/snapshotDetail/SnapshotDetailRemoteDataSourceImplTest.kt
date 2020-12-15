package com.lawmobile.data.datasource.remote.snapshotDetail

import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SnapshotDetailRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val snapshotDetailRemoteDataSourceImpl by lazy {
        SnapshotDetailRemoteDataSourceImpl(cameraService)
    }

    @Test
    fun testGetImageBytesSuccess() {
        val cameraFile: CameraFile = mockk()
        val byte = ByteArray(1)
        coEvery { cameraService.getImageBytes(cameraFile) } returns Result.Success(
            byte
        )
        runBlocking {
            val result =
                snapshotDetailRemoteDataSourceImpl.getImageBytes(cameraFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify { cameraService.getImageBytes(cameraFile) }
    }

    @Test
    fun testGetImageBytesError() {
        val connectFile: CameraFile = mockk()
        coEvery { cameraService.getImageBytes(connectFile) } returns Result.Error(mockk())
        runBlocking {
            val result =
                snapshotDetailRemoteDataSourceImpl.getImageBytes(connectFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { cameraService.getImageBytes(connectFile) }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow() {
        coEvery { cameraService.savePhotoMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            val result = snapshotDetailRemoteDataSourceImpl.savePartnerIdSnapshot(mockk())
            Assert.assertTrue(result is Result.Success)
        }
        coVerify {
            cameraService.savePhotoMetadata(any())
        }
    }

    @Test
    fun testGetInformationOfPhoto() {
        coEvery { cameraService.getPhotoMetadata(any()) } returns Result.Success(mockk())
        runBlocking {
            val result = snapshotDetailRemoteDataSourceImpl.getInformationOfPhoto(mockk())
            Assert.assertTrue(result is Result.Success)
        }

        coVerify {
            cameraService.getPhotoMetadata(any())
        }
    }

    @Test
    fun testGetVideoList() {
        coEvery { cameraService.getListOfVideos() } returns Result.Success(mockk())
        runBlocking {
            val result = snapshotDetailRemoteDataSourceImpl.getVideoList()
            Assert.assertTrue(result is Result.Success)
        }

        coVerify {
            cameraService.getListOfVideos()
        }
    }

    @Test
    fun testGetMetadataOfVideo() {
        coEvery {
            cameraService.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(mockk())
        runBlocking {
            val cameraFile = CameraFile(
                "name",
                "",
                "",
                "folder"
            )
            val result = snapshotDetailRemoteDataSourceImpl.getMetadataOfVideo(cameraFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify {
            cameraService.getVideoMetadata("name", "folder")
        }
    }

    @Test
    fun testSavePartnerIdInAllSnapshots() {
        coEvery { cameraService.saveAllPhotoMetadata(any()) } returns Result.Success(Unit)
        runBlocking { snapshotDetailRemoteDataSourceImpl.savePartnerIdInAllSnapshots(emptyList()) }
        coVerify { cameraService.saveAllPhotoMetadata(emptyList()) }
    }

    @Test
    fun testGetSavedPhotosMetadata() {
        coEvery { cameraService.getMetadataOfPhotos() } returns Result.Success(mockk())
        runBlocking { snapshotDetailRemoteDataSourceImpl.getSavedPhotosMetadata() }
        coVerify { cameraService.getMetadataOfPhotos() }
    }
}