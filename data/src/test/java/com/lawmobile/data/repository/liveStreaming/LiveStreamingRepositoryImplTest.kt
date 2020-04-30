package com.lawmobile.data.repository.liveStreaming

import com.lawmobile.data.datasource.remote.liveStreaming.LiveStreamingRemoteDataSource
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LiveStreamingRepositoryImplTest {

    private val liveStreamingRemoteDataSource: LiveStreamingRemoteDataSource = mockk()

    private val liveStreamingRepositoryImpl: LiveStreamingRepositoryImpl by lazy {
        LiveStreamingRepositoryImpl(liveStreamingRemoteDataSource)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        every { liveStreamingRemoteDataSource.getUrlForLiveStream() } returns "xyz"
        liveStreamingRepositoryImpl.getUrlForLiveStream()
        verify { liveStreamingRemoteDataSource.getUrlForLiveStream() }
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValue() {
        every { liveStreamingRemoteDataSource.getUrlForLiveStream() } returns "xyz"
        val url = liveStreamingRepositoryImpl.getUrlForLiveStream()
        Assert.assertEquals("xyz", url)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValueEmpty() {
        every { liveStreamingRemoteDataSource.getUrlForLiveStream() } returns ""
        val url = liveStreamingRemoteDataSource.getUrlForLiveStream()
        Assert.assertEquals(url, "")
    }

    @Test
    fun testStartRecordVideoFlow() {
        coEvery { liveStreamingRemoteDataSource.startRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRepositoryImpl.startRecordVideo()
        }
        coVerify { liveStreamingRemoteDataSource.startRecordVideo() }
    }

    @Test
    fun testStartRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRemoteDataSource.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.startRecordVideo(), result)
        }
    }

    @Test
    fun testStartRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { liveStreamingRemoteDataSource.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.startRecordVideo(), result)
        }
    }


    @Test
    fun testStopRecordVideoFlow() {
        coEvery { liveStreamingRemoteDataSource.stopRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRepositoryImpl.stopRecordVideo()
        }
        coVerify { liveStreamingRemoteDataSource.stopRecordVideo() }
    }

    @Test
    fun testStopRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRemoteDataSource.stopRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.stopRecordVideo(), result)
        }
    }

    @Test
    fun testStopRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { liveStreamingRemoteDataSource.stopRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.stopRecordVideo(), result)
        }
    }


    @Test
    fun testTakePhotoFlow() {
        coEvery { liveStreamingRemoteDataSource.takePhoto() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingRepositoryImpl.takePhoto()
        }

        coEvery { liveStreamingRemoteDataSource.takePhoto() }
    }

    @Test
    fun testTakePhotoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRemoteDataSource.takePhoto() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.takePhoto(), result)
        }
    }

    @Test
    fun testTakePhotoFailed() {
        val result = Result.Error(Exception(""))
        coEvery { liveStreamingRemoteDataSource.takePhoto() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingRepositoryImpl.takePhoto(), result)
        }
    }

}