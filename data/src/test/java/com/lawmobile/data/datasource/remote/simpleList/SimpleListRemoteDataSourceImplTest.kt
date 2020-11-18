package com.lawmobile.data.datasource.remote.simpleList

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

internal class SimpleListRemoteDataSourceImplTest {

    private val cameraConnectService: CameraConnectService = mockk()

    private val simpleListRemoteDataSourceImpl by lazy {
        SimpleListRemoteDataSourceImpl(cameraConnectService)
    }

    @Test
    fun testGetSnapshotListFlow() {
        coEvery { cameraConnectService.getListOfImages() } returns Result.Success(mockk())
        runBlocking {
            simpleListRemoteDataSourceImpl.getSnapshotList()
        }
        coVerify { cameraConnectService.getListOfImages() }
    }

    @Test
    fun testGetSnapshotListSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
        }
        val result = Result.Success(cameraResponse)
        coEvery { cameraConnectService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(simpleListRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetSnapshotListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraConnectService.getListOfImages() } returns result
        runBlocking {
            Assert.assertEquals(simpleListRemoteDataSourceImpl.getSnapshotList(), result)
        }
    }

    @Test
    fun testGetVideoListFlow() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
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
            simpleListRemoteDataSourceImpl.getVideoList()
        }
        coVerify {
            cameraConnectService.getListOfVideos()
            cameraConnectService.getVideoMetadata(any(), any())
        }
    }

    @Test
    fun testGetVideoListSuccess() {
        val cameraConnectFile: CameraConnectFile = mockk(relaxed = true)
        val cameraResponse: CameraConnectFileResponseWithErrors = mockk {
            every { items } returns arrayListOf(cameraConnectFile)
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
            Assert.assertEquals(simpleListRemoteDataSourceImpl.getVideoList(), result)
        }
    }

    @Test
    fun testGetVideoListFailed() {
        val result = Result.Error(mockk())
        coEvery { cameraConnectService.getListOfVideos() } returns result
        runBlocking {
            Assert.assertEquals(simpleListRemoteDataSourceImpl.getVideoList(), result)
        }
    }
}