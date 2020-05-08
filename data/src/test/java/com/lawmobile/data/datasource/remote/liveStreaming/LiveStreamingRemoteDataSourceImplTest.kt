package com.lawmobile.data.datasource.remote.liveStreaming

import com.lawmobile.data.InstantExecutorExtension
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class LiveStreamingRemoteDataSourceImplTest {

    private val cameraConnectService: CameraConnectService = mockk()

    private val liveStreamingRemoteDataSourceImpl by lazy {
        LiveStreamingRemoteDataSourceImpl(cameraConnectService)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        every { cameraConnectService.getUrlForLiveStream() } returns "xyz"
        liveStreamingRemoteDataSourceImpl.getUrlForLiveStream()
        verify { cameraConnectService.getUrlForLiveStream() }
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValue() {
        every { cameraConnectService.getUrlForLiveStream() } returns "xyz"
        val url = liveStreamingRemoteDataSourceImpl.getUrlForLiveStream()
        Assert.assertEquals("xyz", url)
    }

    @Test
    fun testStartRecordVideoFlow() {
        coEvery { cameraConnectService.startRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRemoteDataSourceImpl.startRecordVideo()
        }
        coVerify { cameraConnectService.startRecordVideo() }
    }

    @Test
    fun testStartRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraConnectService.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.startRecordVideo(), result)
        }
    }

    @Test
    fun testStartRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { cameraConnectService.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.startRecordVideo(), result)
        }
    }


    @Test
    fun testStopRecordVideoFlow() {
        coEvery { cameraConnectService.stopRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRemoteDataSourceImpl.stopRecordVideo()
        }
        coVerify { cameraConnectService.stopRecordVideo() }
    }

    @Test
    fun testStopRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraConnectService.stopRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.stopRecordVideo(), result)
        }
    }

    @Test
    fun testStopRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { cameraConnectService.stopRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.stopRecordVideo(), result)
        }
    }

    @Test
    fun testTakePhotoFlow() {
        coEvery { cameraConnectService.takePhoto() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRemoteDataSourceImpl.takePhoto()
        }

        coEvery { cameraConnectService.takePhoto() }
    }

    @Test
    fun testTakePhotoSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraConnectService.takePhoto() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.takePhoto(), result)
        }
    }

    @Test
    fun testTakePhotoFailed() {
        val result = Result.Error(Exception(""))
        coEvery { cameraConnectService.takePhoto() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.takePhoto(), result)
        }
    }

}