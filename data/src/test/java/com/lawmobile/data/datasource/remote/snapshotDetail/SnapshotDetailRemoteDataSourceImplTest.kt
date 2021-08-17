package com.lawmobile.data.datasource.remote.snapshotDetail

import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SnapshotDetailRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }
    private val snapshotDetailRemoteDataSourceImpl by lazy {
        SnapshotDetailRemoteDataSourceImpl(cameraServiceFactory)
    }

    @Test
    fun testGetImageBytesSuccess() {
        val cameraFile: CameraFile = mockk()
        val byte = ByteArray(1)

        coEvery { cameraService.getImageBytes(cameraFile) } returns Result.Success(byte)
        runBlocking {
            val result = snapshotDetailRemoteDataSourceImpl.getImageBytes(cameraFile)
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.getImageBytes(cameraFile) }
    }

    @Test
    fun testGetImageBytesError() {
        val connectFile: CameraFile = mockk()
        coEvery { cameraService.getImageBytes(connectFile) } returns Result.Error(mockk())
        runBlocking {
            val result = snapshotDetailRemoteDataSourceImpl.getImageBytes(connectFile)
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
        coVerify { cameraService.savePhotoMetadata(any()) }
    }

    @Test
    fun testGetInformationOfPhoto() {
        coEvery { cameraService.getPhotoMetadata(any()) } returns Result.Success(mockk())
        runBlocking {
            val result = snapshotDetailRemoteDataSourceImpl.getInformationOfPhoto(mockk())
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.getPhotoMetadata(any()) }
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
