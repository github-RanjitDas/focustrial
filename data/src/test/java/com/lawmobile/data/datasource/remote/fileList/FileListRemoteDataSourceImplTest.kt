package com.lawmobile.data.datasource.remote.fileList

import com.lawmobile.data.InstantExecutorExtension
import com.lawmobile.data.entities.VideoListMetadata
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFileResponseWithErrors
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
internal class FileListRemoteDataSourceImplTest {

    private val cameraConnectService: CameraConnectService = mockk()

    private val fileListRemoteDataSourceImpl by lazy {
        FileListRemoteDataSourceImpl(cameraConnectService)
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { cameraConnectService.getListOfImages() } returns Result.Success(mockk())
        runBlocking {
            fileListRemoteDataSourceImpl.getSnapshotList()
        }
        coVerify { cameraConnectService.getListOfImages() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk{
            every { items } returns  arrayListOf(cameraConnectFile)
        }
        val result = Result.Success(cameraResponse)
        coEvery { cameraConnectService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraConnectService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetVideoListFlow() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk{
            every { items } returns  arrayListOf(cameraConnectFile)
        }
        coEvery { cameraConnectService.getListOfVideos() } returns Result.Success(cameraResponse)

        VideoListMetadata.metadataList = mutableListOf()
        coEvery {
            cameraConnectService.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(mockk(relaxed = true))
        runBlocking {
            fileListRemoteDataSourceImpl.getVideoList()
        }
        coVerify {
            cameraConnectService.getListOfVideos()
            cameraConnectService.getVideoMetadata(any(), any())
        }
    }

    @Test
    fun testGetVideoListSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk{
            every { items } returns  arrayListOf(cameraConnectFile)
        }
        val result = Result.Success(cameraResponse)
        coEvery { cameraConnectService.getListOfVideos() } returns result
        coEvery {
            cameraConnectService.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(mockk(relaxed = true))
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getVideoList(), result)
        }
    }

    @Test
    fun testGetVideoListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraConnectService.getListOfVideos() } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.getVideoList(), result)
        }
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
    fun testSavePartnerIdSnapshotSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraConnectService.savePhotoMetadata(any()) } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.savePartnerIdSnapshot(mockk()), result)
        }
    }

    @Test
    fun testSavePartnerIdSnapshotFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraConnectService.savePhotoMetadata(any()) } returns result
        runBlocking {
            Assert.assertEquals(fileListRemoteDataSourceImpl.savePartnerIdSnapshot(mockk()), result)
        }
    }
}