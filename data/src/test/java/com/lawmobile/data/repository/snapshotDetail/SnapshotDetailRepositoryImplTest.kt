package com.lawmobile.data.repository.snapshotDetail

import com.lawmobile.data.datasource.remote.snapshotDetail.SnapshotDetailRemoteDataSource
import com.lawmobile.data.mappers.impl.FileMapper.toDomain
import com.lawmobile.domain.entities.FileList
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.PhotoInformation
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class SnapshotDetailRepositoryImplTest {

    private val snapshotDetailRemoteDataSource: SnapshotDetailRemoteDataSource = mockk()
    private val dispatcher = TestCoroutineDispatcher()

    private val snapshotDetailRepositoryImpl by lazy {
        SnapshotDetailRepositoryImpl(snapshotDetailRemoteDataSource)
    }

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetInformationResourcesVideoSuccess() = runBlockingTest {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")
        val byte = ByteArray(1)

        coEvery { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) } returns
            Result.Success(byte)

        val result = snapshotDetailRepositoryImpl.getImageBytes(
            cameraConnectFile.toDomain()
        ) as Result.Success
        Assert.assertEquals(result.data, byte)

        coVerify { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testGetImageBytesError() = runBlockingTest {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) } returns
            Result.Error(Exception(""))

        val result = snapshotDetailRepositoryImpl.getImageBytes(
            cameraConnectFile.toDomain()
        )
        Assert.assertTrue(result is Result.Error)

        coVerify { snapshotDetailRemoteDataSource.getImageBytes(cameraConnectFile) }
    }

    @Test
    fun testSavePartnerIdSnapshotFlow() = dispatcher.runBlockingTest {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { snapshotDetailRemoteDataSource.savePartnerIdSnapshot(any()) } returns
            Result.Success(Unit)
        coEvery { snapshotDetailRemoteDataSource.getSavedPhotosMetadata() } returns
            Result.Success(emptyList())
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns
            Result.Success(Unit)

        FileList.imageMetadataList = ArrayList()

        snapshotDetailRepositoryImpl.saveSnapshotPartnerId(
            cameraConnectFile.toDomain(),
            "partnerId"
        )

        coVerify {
            snapshotDetailRemoteDataSource.savePartnerIdSnapshot(any())
            snapshotDetailRemoteDataSource.getSavedPhotosMetadata()
            snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(any())
        }
    }

    @Test
    fun testSavePartnerIdSnapshotSuccessWithInformationInFileList() = dispatcher.runBlockingTest {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { snapshotDetailRemoteDataSource.savePartnerIdSnapshot(any()) } returns
            Result.Success(Unit)
        coEvery { snapshotDetailRemoteDataSource.getSavedPhotosMetadata() } returns
            Result.Success(emptyList())
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns
            Result.Success(Unit)

        val response = snapshotDetailRepositoryImpl.saveSnapshotPartnerId(
            cameraConnectFile.toDomain(),
            "partnerId"
        )

        val item = FileList.findAndGetImageMetadata("fileName.PNG")
        Assert.assertTrue(response is Result.Success)
        Assert.assertEquals(item?.photoMetadata?.metadata?.partnerID, "partnerId")
    }

    @Test
    fun testSavePartnerIdSnapshotError() = dispatcher.runBlockingTest {
        val cameraConnectFile = CameraFile("fileName.PNG", "date", "path", "nameFolder/")

        coEvery { snapshotDetailRemoteDataSource.savePartnerIdSnapshot(any()) } returns
            Result.Error(Exception(""))
        coEvery { snapshotDetailRemoteDataSource.getSavedPhotosMetadata() } returns
            Result.Success(emptyList())
        coEvery { snapshotDetailRemoteDataSource.savePartnerIdInAllSnapshots(any()) } returns
            Result.Success(Unit)

        val response = snapshotDetailRepositoryImpl.saveSnapshotPartnerId(
            cameraConnectFile.toDomain(),
            "partnerId"
        )
        Assert.assertTrue(response is Result.Error)
    }

    @Test
    fun testGetInformationOfPhotoError() = runBlockingTest {
        // FileList.imageMetadataList = ArrayList()
        coEvery { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) } returns Result.Error(
            mockk()
        )
        val cameraConnectFile = CameraFile("name", "date", "path", "nameFol")

        val response = snapshotDetailRepositoryImpl.getInformationOfPhoto(
            cameraConnectFile.toDomain()
        )
        Assert.assertTrue(response is Result.Error)
    }

    @Test
    fun testGetInformationOfPhotoSuccess() = runBlockingTest {
        // FileList.listOfMetadataImages = ArrayList()
        val cameraConnectPhotoMetadata = PhotoInformation(fileName = "name")
        coEvery { snapshotDetailRemoteDataSource.getInformationOfPhoto(any()) } returns Result.Success(
            cameraConnectPhotoMetadata
        )
        val cameraSend = CameraFile("name", "date", "path", "nameFol")

        val response = snapshotDetailRepositoryImpl.getInformationOfPhoto(cameraSend.toDomain())
        Assert.assertTrue(response is Result.Success)
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
                cameraConnectFile.toDomain()
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
                cameraSend.toDomain()
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
