package com.lawmobile.data.datasource.remote.liveStreaming

import com.lawmobile.data.InstantExecutorExtension
import com.safefleet.mobile.avml.cameras.x1.CameraDataSource
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

    private val cameraDataSource: CameraDataSource = mockk ()

    private val liveStreamingRemoteDataSourceImpl by lazy {
        LiveStreamingRemoteDataSourceImpl(cameraDataSource)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        every { cameraDataSource.getUrlForLiveStream() } returns "xyz"
        liveStreamingRemoteDataSourceImpl.getUrlForLiveStream()
        verify { cameraDataSource.getUrlForLiveStream() }
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValue() {
        every { cameraDataSource.getUrlForLiveStream() } returns "xyz"
        val url = liveStreamingRemoteDataSourceImpl.getUrlForLiveStream()
        Assert.assertEquals("xyz", url)
    }

    @Test
    fun testStartRecordVideoFlow() {
        coEvery { cameraDataSource.startRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRemoteDataSourceImpl.startRecordVideo()
        }
        coVerify { cameraDataSource.startRecordVideo() }
    }

    @Test
    fun testStartRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraDataSource.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.startRecordVideo(), result)
        }
    }

    @Test
    fun testStartRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { cameraDataSource.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.startRecordVideo(), result)
        }
    }


    @Test
    fun testStopRecordVideoFlow() {
        coEvery { cameraDataSource.stopRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRemoteDataSourceImpl.stopRecordVideo()
        }
        coVerify { cameraDataSource.stopRecordVideo() }
    }

    @Test
    fun testStopRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraDataSource.stopRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.stopRecordVideo(), result)
        }
    }

    @Test
    fun testStopRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { cameraDataSource.stopRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.stopRecordVideo(), result)
        }
    }

    @Test
    fun testTakePhotoFlow() {
        coEvery { cameraDataSource.takePhoto() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRemoteDataSourceImpl.takePhoto()
        }

        coEvery { cameraDataSource.takePhoto() }
    }

    @Test
    fun testTakePhotoSuccess() {
        val result = Result.Success(Unit)
        coEvery { cameraDataSource.takePhoto() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.takePhoto(), result)
        }
    }

    @Test
    fun testTakePhotoFailed() {
        val result = Result.Error(Exception(""))
        coEvery { cameraDataSource.takePhoto() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRemoteDataSourceImpl.takePhoto(), result)
        }
    }

}