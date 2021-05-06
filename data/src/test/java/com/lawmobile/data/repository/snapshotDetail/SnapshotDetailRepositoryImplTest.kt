package com.lawmobile.data.repository.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.data.mappers.FileMapper
import com.lawmobile.domain.entities.FileList
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result
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
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")
        val byte = ByteArray(1)

        coEvery { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) } returns
            Result.Success(byte)

        runBlocking {
            val result = snapshotDetailRepositoryImpl.getImageBytes(
                FileMapper.cameraToDomain(cameraConnectFile)
            ) as Result.Success
            Assert.assertEquals(result.data, byte)
        }

        coVerify { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testGetImageBytesError() {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) } returns
            Result.Error(Exception(""))

        runBlocking {
            val result = snapshotDetailRepositoryImpl.getImageBytes(
                FileMapper.cameraToDomain(cameraConnectFile)
            )
            Assert.assertTrue(result is Result.Error)
        }

        coVerify { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow() {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { snapshotDetailRemoteDataSource.savePartnerIdSnapshot(any()) } returns
            Result.Success(Unit)
        coEvery { snapshotDetailRemoteDataSource.getSavedPhotosMetadata() } returns
            Result.Success(emptyList())
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns
            Result.Success(Unit)

        FileList.imageMetadataList = ArrayList()

        runBlocking {
            snapshotDetailRepositoryImpl.saveSnapshotPartnerId(
                FileMapper.cameraToDomain(cameraConnectFile),
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
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { snapshotDetailRemoteDataSource.savePartnerIdSnapshot(any()) } returns
            Result.Success(Unit)
        coEvery { snapshotDetailRemoteDataSource.getSavedPhotosMetadata() } returns
            Result.Success(emptyList())
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns
            Result.Success(Unit)

        runBlocking {
            val response = snapshotDetailRepositoryImpl.saveSnapshotPartnerId(
                FileMapper.cameraToDomain(cameraConnectFile),
                "partnerId"
            )
            val item = FileList.getMetadataOfImageInList("fileName.PNG")
            Assert.assertTrue(response is Result.Success)
            Assert.assertEquals(item?.photoMetadata?.metadata?.partnerID, "partnerId")
        }
    }

    @Test
    fun testSavePartnerIdSnapshotError() {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { snapshotDetailRemoteDataSource.savePartnerIdSnapshot(any()) } returns
            Result.Error(Exception(""))
        coEvery { snapshotDetailRemoteDataSource.getSavedPhotosMetadata() } returns
            Result.Success(emptyList())
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns
            Result.Success(Unit)

        runBlocking {
            val response = snapshotDetailRepositoryImpl.saveSnapshotPartnerId(
                FileMapper.cameraToDomain(cameraConnectFile),
                "partnerId"
            )
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testGetInformationOfPhotoError() {
        // FileList.imageMetadataList = ArrayList()
        coEvery { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) } returns Result.Error(
            mockk()
        )
        val cameraConnectFile = CameraFile("name", "date", "path", "nameFol")

        runBlocking {
            val response = snapshotDetailRepositoryImpl.getInformationOfPhoto(
                FileMapper.cameraToDomain(cameraConnectFile)
            )
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testGetInformationOfPhotoSuccess() {
        // FileList.listOfMetadataImages = ArrayList()
        val cameraConnectPhotoMetadata = PhotoInformation(fileName = "name")
        coEvery { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) } returns Result.Success(
            cameraConnectPhotoMetadata
        )
        val cameraSend = CameraFile("name", "date", "path", "nameFol")

        runBlocking {
            val response = snapshotDetailRepositoryImpl.getInformationOfPhoto(FileMapper.cameraToDomain(cameraSend))
            Assert.assertTrue(response is Result.Success)
        }
    }

    /*@Test
    fun testGetInformationOfPhotoErrorInVideoList() {
        val cameraConnectFile = CameraConnectFile("name", "date", "path", "nameFol")
        FileList.imageMetadataList = ArrayList()

        coEvery { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) } returns
                Result.Success(CameraConnectPhotoMetadata("fileName.PNG"))
        coEvery { snapshotDetailRemoteDataSource.getVideoList() } returns Result.Error(mockk())

        runBlocking {
            val response = snapshotDetailRepositoryImpl.getInformationOfPhoto(
                FileMapper.cameraToDomain(cameraConnectFile)
            )
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testGetInformationOfPhotoErrorInVideoMetadata() {
        val cameraConnectFile = CameraConnectFile("fileName.MP4", "date", "path", "nameFolder/")
        val responseCamera = CameraConnectFileResponseWithErrors().apply {
            items.add(cameraConnectFile)
            errors.clear()
        }
        val cameraSend = CameraConnectFile("name", "date", "path", "nameFol")
        FileList.imageMetadataList = ArrayList()

        coEvery { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) } returns
                Result.Success(CameraConnectPhotoMetadata("fileName.PNG"))
        coEvery { snapshotDetailRemoteDataSource.getVideoList() } returns
                Result.Success(responseCamera)
        coEvery { snapshotDetailRemoteDataSource.getMetadataOfVideo(any()) } returns
                Result.Error(mockk())

        runBlocking {
            val response = snapshotDetailRepositoryImpl.getInformationOfPhoto(
                FileMapper.cameraToDomain(cameraSend)
            )
            Assert.assertTrue(response is Result.Error)
        }
    }

    @Test
    fun testGetInformationOfPhotoFlowInformationPhoto() {
        val domainCameraFile = DomainCameraFile("fileName.MP4", "date", "path", "nameFolder/")
        FileList.imageMetadataList = ArrayList()

        coEvery { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) } returns
                Result.Success(CameraConnectPhotoMetadata("fileName.PNG"))
        coEvery { snapshotDetailRemoteDataSource.getVideoList() } returns Result.Error(mockk())

        runBlocking { snapshotDetailRepositoryImpl.getInformationOfPhoto(domainCameraFile) }

        coVerify { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) }
    }*/
}
