package com.lawmobile.data.datasource.remote.snapshotDetail

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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SnapshotDetailRemoteDataSourceImplTest {

    private val cameraConnectService: CameraConnectService = mockk()
    private val snapshotDetailRemoteDataSourceImpl by lazy {
        SnapshotDetailRemoteDataSourceImpl(cameraConnectService)
    }

    @Test
    fun testGetImageBytesSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk()
        val byte = ByteArray(1)
        coEvery { cameraConnectService.getImageBytes(cameraConnectFile) } returns Result.Success(
            byte
        )
        runBlocking {
            val result =
                snapshotDetailRemoteDataSourceImpl.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify { cameraConnectService.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testGetImageBytesError() {
        val cameraConnectFile: CameraConnectFile = mockk()
        coEvery { cameraConnectService.getImageBytes(cameraConnectFile) } returns Result.Error(mockk())
        runBlocking {
            val result =
                snapshotDetailRemoteDataSourceImpl.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { cameraConnectService.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow() {
        coEvery { cameraConnectService.savePhotoMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            val result = snapshotDetailRemoteDataSourceImpl.savePartnerIdSnapshot(mockk())
            Assert.assertTrue(result is Result.Success)
        }
        coVerify {
            cameraConnectService.savePhotoMetadata(any())
        }
    }

    @Test
    fun testGetInformationOfPhoto() {
        coEvery { cameraConnectService.getPhotoMetadata(any()) } returns Result.Success(mockk())
        runBlocking {
            val result = snapshotDetailRemoteDataSourceImpl.getInformationOfPhoto(mockk())
            Assert.assertTrue(result is Result.Success)
        }

        coVerify {
            cameraConnectService.getPhotoMetadata(any())
        }
    }

    @Test
    fun testGetVideoList() {
        coEvery { cameraConnectService.getListOfVideos() } returns Result.Success(mockk())
        runBlocking {
            val result = snapshotDetailRemoteDataSourceImpl.getVideoList()
            Assert.assertTrue(result is Result.Success)
        }

        coVerify {
            cameraConnectService.getListOfVideos()
        }
    }

    @Test
    fun testGetMetadataOfVideo() {
        coEvery {
            cameraConnectService.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(mockk())
        runBlocking {
            val cameraConnectFile = CameraConnectFile(
                "name",
                "",
                "",
                "folder"
            )
            val result = snapshotDetailRemoteDataSourceImpl.getMetadataOfVideo(cameraConnectFile)
            Assert.assertTrue(result is Result.Success)
        }

        coVerify {
            cameraConnectService.getVideoMetadata("name", "folder")
        }
    }

    @Test
    fun testSavePartnerIdInAllSnapshots() {
        coEvery { cameraConnectService.saveAllPhotoMetadata(any()) } returns Result.Success(Unit)
        runBlocking { snapshotDetailRemoteDataSourceImpl.savePartnerIdInAllSnapshots(emptyList()) }
        coVerify { cameraConnectService.saveAllPhotoMetadata(emptyList()) }
    }

    @Test
    fun testGetSavedPhotosMetadata() {
        coEvery { cameraConnectService.getMetadataOfPhotos() } returns Result.Success(mockk())
        runBlocking { snapshotDetailRemoteDataSourceImpl.getSavedPhotosMetadata() }
        coVerify { cameraConnectService.getMetadataOfPhotos() }
    }
}