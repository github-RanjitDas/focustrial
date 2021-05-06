package com.lawmobile.data.datasource.remote.liveStreaming

import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.external_hardware.cameras.CameraService
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LiveStreamingRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }

    private val liveStreamingRemoteDataSourceImpl by lazy {
        LiveStreamingRemoteDataSourceImpl(cameraServiceFactory)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        every { cameraService.getUrlForLiveStream() } returns "xyz"
        liveStreamingRemoteDataSourceImpl.getUrlForLiveStream()
        verify { cameraService.getUrlForLiveStream() }
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValue() {
        every { cameraService.getUrlForLiveStream() } returns "xyz"
        val url = liveStreamingRemoteDataSourceImpl.getUrlForLiveStream()
        Assert.assertEquals("xyz", url)
    }

    @Test
    fun testStartRecordVideoFlow() {
        coEvery { cameraService.startRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRemoteDataSourceImpl.startRecordVideo()
        }
        coVerify { cameraService.startRecordVideo() }
    }

    @Test
    fun testStartRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraService.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.startRecordVideo(), result)
        }
    }

    @Test
    fun testStartRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { cameraService.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.startRecordVideo(), result)
        }
    }

    @Test
    fun testStopRecordVideoFlow() {
        coEvery { cameraService.stopRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRemoteDataSourceImpl.stopRecordVideo()
        }
        coVerify { cameraService.stopRecordVideo() }
    }

    @Test
    fun testStopRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraService.stopRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.stopRecordVideo(), result)
        }
    }

    @Test
    fun testStopRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { cameraService.stopRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.stopRecordVideo(), result)
        }
    }

    @Test
    fun testTakePhotoFlow() {
        coEvery { cameraService.takePhoto() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRemoteDataSourceImpl.takePhoto()
        }

        coEvery { cameraService.takePhoto() }
    }

    @Test
    fun testTakePhotoSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraService.takePhoto() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.takePhoto(), result)
        }
    }

    @Test
    fun testTakePhotoFailed() {
        val result = Result.Error(Exception(""))
        coEvery { cameraService.takePhoto() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.takePhoto(), result)
        }
    }

    @Test
    fun testGetCatalogInfoSuccess() {
        coEvery { cameraService.getCatalogInfo() } returns Result.Success(mockk())
        runBlocking { liveStreamingRemoteDataSourceImpl.getCatalogInfo() }
        coVerify { cameraService.getCatalogInfo() }
    }

    @Test
    fun testGetBatteryLevel() {
        val result = Result.Success(10)
        coEvery { cameraService.getBatteryLevel() } returns result
        runBlocking {
            val response = liveStreamingRemoteDataSourceImpl.getBatteryLevel()
            Assert.assertEquals(response, result)
        }
        coVerify { cameraService.getBatteryLevel() }
    }

    @Test
    fun testGetFreeStorage() {
        val result = Result.Success("10000")
        coEvery { cameraService.getFreeStorage() } returns result
        runBlocking {
            val response = liveStreamingRemoteDataSourceImpl.getFreeStorage()
            Assert.assertEquals(response, result)
        }
        coVerify { cameraService.getFreeStorage() }
    }

    @Test
    fun testGetTotalStorage() {
        val result = Result.Success("10000")
        coEvery { cameraService.getTotalStorage() } returns result
        runBlocking {
            val response = liveStreamingRemoteDataSourceImpl.getTotalStorage()
            Assert.assertEquals(response, result)
        }
        coVerify { cameraService.getTotalStorage() }
    }

    @Test
    fun disconnectCameraFlow() {
        coEvery { cameraService.disconnectCamera() } returns Result.Success(Unit)
        runBlocking { liveStreamingRemoteDataSourceImpl.disconnectCamera() }
        coVerify { cameraService.disconnectCamera() }
    }
}
