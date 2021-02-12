package com.lawmobile.data.datasource.remote.simpleList

import com.lawmobile.domain.entities.VideoListMetadata
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.external_hardware.cameras.entities.CameraFile
import com.safefleet.mobile.external_hardware.cameras.entities.FileResponseWithErrors
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

internal class SimpleListRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()

    private val simpleListRemoteDataSourceImpl by lazy {
        SimpleListRemoteDataSourceImpl(cameraService)
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { cameraService.getListOfImages() } returns Result.Success(mockk())
        runBlocking {
            simpleListRemoteDataSourceImpl.getSnapshotList()
        }
        coVerify { cameraService.getListOfImages() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val cameraFile: CameraFile = mockk(relaxed = true)
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraFile)
        }
        val result = Result.Success(cameraResponse)
        coEvery { cameraService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(simpleListRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(simpleListRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetVideoListFlow() {
        val cameraFile: CameraFile = mockk(relaxed = true)
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraFile)
        }
        coEvery { cameraService.getListOfVideos() } returns Result.Success(cameraResponse)

        VideoListMetadata.metadataList = mutableListOf()
        coEvery {
            cameraService.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(mockk(relaxed = true))
        runBlocking {
            simpleListRemoteDataSourceImpl.getVideoList()
        }
        coVerify {
            cameraService.getListOfVideos()
            cameraService.getVideoMetadata(any(), any())
        }
    }

    @Test
    fun testGetVideoListSuccess() {
        val cameraFile: CameraFile = mockk(relaxed = true)
        val cameraResponse: FileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraFile)
        }
        val result = Result.Success(cameraResponse)
        coEvery { cameraService.getListOfVideos() } returns result
        coEvery {
            cameraService.getVideoMetadata(
                any(),
                any()
            )
        } returns Result.Success(mockk(relaxed = true))
        runBlocking {
            Assert.assertEquals(simpleListRemoteDataSourceImpl.getVideoList(), result)
        }
    }

    @Test
    fun testGetVideoListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraService.getListOfVideos() } returns result
        runBlocking {
            Assert.assertEquals(simpleListRemoteDataSourceImpl.getVideoList(), result)
        }
    }
}