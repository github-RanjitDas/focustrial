package com.lawmobile.data.repository.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.data.entities.FileList
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.avml.cameras.entities.CameraConnectPhotoMetadata
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

class SnapshotDetailRepositoryImplTest {

    private val snapshotDetailRemoteDataSource: SnapshotDetailRemoteDataSource = mockk()
    private val snapshotDetailRepositoryImpl by lazy {
        SnapshotDetailRepositoryImpl(snapshotDetailRemoteDataSource)
    }

    @Test
    fun testGetInformationResourcesVideoSuccess() {
        val cameraConnectFile = CameraConnectFile("fileName.PNG", "date", "path", "nameFolder/")
        val byte = ByteArray(1)
        coEvery { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) } returns Result.Success(
            byte
        )
        runBlocking {
            val result =
                snapshotDetailRepositoryImpl.getImageBytes(cameraConnectFile) as Result.Success
            Assert.assertEquals(result.data, byte)
        }

        coVerify { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testGetImageBytesError() {
        val cameraConnectFile = CameraConnectFile("fileName.PNG", "date", "path", "nameFolder/")
        coEvery { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) } returns Result.Error(
            Exception("")
        )
        runBlocking {
            val result =
                snapshotDetailRepositoryImpl.getImageBytes(cameraConnectFile)
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow() {
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdSnapshot(any()) } returns Result.Success(
            Unit
        )
        coEvery { snapshotDetailRemoteDataSource.getSavedPhotosMetadata() } returns Result.Success(
            emptyList()
        )
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns Result.Success(Unit)
        val cameraConnectFile = CameraConnectFile("fileName.PNG", "date", "path", "nameFolder/")
        FileList.listOfMetadataImages = ArrayList()
        runBlocking {
            snapshotDetailRepositoryImpl.saveSnapshotPartnerId(
                cameraConnectFile,
                "partnerId"
            )
        }
        coVerify {
            snapshotDetailRemoteDataSource.savePartnerIdSnapshot(any())
            snapshotDetailRemoteDataSource.getSavedPhotosMetadata()
            snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(any())
        }
    }

    @Test
    fun testSavePartnerIdSnapshotSuccessWithInformationInFileList() {
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdSnapshot(any()) } returns Result.Success(
            Unit
        )
        coEvery { snapshotDetailRemoteDataSource.getSavedPhotosMetadata() } returns Result.Success(
            emptyList()
        )
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns Result.Success(Unit)
        runBlocking {
            val cameraConnectFile = CameraConnectFile("fileName.PNG", "date", "path", "nameFolder/")
            val response =
                snapshotDetailRepositoryImpl.saveSnapshotPartnerId(cameraConnectFile, "partnerId")
            Assert.assertTrue(response is Result.Success)
            val item = FileList.getItemInListImageOfMetadata("fileName.PNG")
            Assert.assertEquals(item?.cameraConnectPhotoMetadata?.metadata?.partnerID, "partnerId")
        }
    }

    @Test
    fun testSavePartnerIdSnapshotError() {
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdSnapshot(any()) } returns Result.Error(
            Exception("")
        )
        coEvery { snapshotDetailRemoteDataSource.getSavedPhotosMetadata() } returns Result.Success(
            emptyList()
        )
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns Result.Success(Unit)
        val cameraConnectFile = CameraConnectFile("fileName.PNG", "date", "path", "nameFolder/")
        runBlocking {
            val response = snapshotDetailRepositoryImpl.saveSnapshotPartnerId(
                cameraConnectFile,
                "partnerId"
            )
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testGetInformationOfPhotoError() {
        FileList.listOfMetadataImages = ArrayList()
        coEvery { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) } returns Result.Error(
            mockk()
        )
        val cameraSend = CameraConnectFile("name", "date", "path", "nameFol")

        runBlocking {
            val response = snapshotDetailRepositoryImpl.getInformationOfPhoto(cameraSend)
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testGetInformationOfPhotoErrorInVideoList() {
        FileList.listOfMetadataImages = ArrayList()
        coEvery { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) } returns Result.Success(
            CameraConnectPhotoMetadata("fileName.PNG")
        )
        coEvery { snapshotDetailRemoteDataSource.getVideoList() } returns Result.Error(mockk())

        val cameraSend = CameraConnectFile("name", "date", "path", "nameFol")

        runBlocking {
            val response = snapshotDetailRepositoryImpl.getInformationOfPhoto(cameraSend)
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testGetInformationOfPhotoErrorInVideoMetadata() {
        FileList.listOfMetadataImages = ArrayList()
        coEvery { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) } returns Result.Success(
            CameraConnectPhotoMetadata("fileName.PNG")
        )
        val cameraConnectFile = CameraConnectFile("fileName.MP4", "date", "path", "nameFolder/")
        val responseCamera = CameraConnectFileResponseWithErrors()
        responseCamera.items.add(cameraConnectFile)
        responseCamera.errors.clear()
        coEvery { snapshotDetailRemoteDataSource.getVideoList() } returns Result.Success(
            responseCamera
        )

        coEvery { snapshotDetailRemoteDataSource.getMetadataOfVideo(any()) } returns Result.Error(
            mockk()
        )
        val cameraSend = CameraConnectFile("name", "date", "path", "nameFol")

        runBlocking {
            val response = snapshotDetailRepositoryImpl.getInformationOfPhoto(cameraSend)
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testGetInformationOfPhotoFlowInformationPhoto() {
        FileList.listOfMetadataImages = ArrayList()
        coEvery { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) } returns Result.Success(
            CameraConnectPhotoMetadata("fileName.PNG")
        )
        val cameraConnectFile = CameraConnectFile("fileName.MP4", "date", "path", "nameFolder/")
        val responseCamera = CameraConnectFileResponseWithErrors()
        responseCamera.items.add(cameraConnectFile)
        responseCamera.errors.clear()
        coEvery { snapshotDetailRemoteDataSource.getVideoList() } returns Result.Error(mockk())
        val cameraSend = CameraConnectFile("name", "date", "path", "nameFol")
        runBlocking {
            snapshotDetailRepositoryImpl.getInformationOfPhoto(cameraSend)
        }

        coVerify { snapshotDetailRemoteDataSource.getInformationOfPhoto(cameraSend) }
    }
}