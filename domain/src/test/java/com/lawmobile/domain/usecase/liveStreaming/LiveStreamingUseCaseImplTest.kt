package com.lawmobile.domain.usecase.liveStreaming

import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LiveStreamingUseCaseImplTest {

    private val liveStreamingRepository: LiveStreamingRepository = mockk {
        every { getUrlForLiveStream() } returns String()
    }

    private val liveStreamingUseCaseImpl: LiveStreamingUseCaseImpl by lazy {
        LiveStreamingUseCaseImpl(liveStreamingRepository)
    }

    @Test
    fun testGetUrlForLiveStreamVerifyFlow() {
        liveStreamingUseCaseImpl.getUrlForLiveStream()
        verify { liveStreamingUseCaseImpl.getUrlForLiveStream() }
    }

    @Test
    fun testStartRecordVideoFlow() {
        coEvery { liveStreamingRepository.startRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingUseCaseImpl.startRecordVideo()
        }
        coVerify { liveStreamingRepository.startRecordVideo() }
    }

    @Test
    fun testStartRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRepository.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingUseCaseImpl.startRecordVideo(), result)
        }
    }

    @Test
    fun testStartRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { liveStreamingRepository.startRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingUseCaseImpl.startRecordVideo(), result)
        }
    }


    @Test
    fun testStopRecordVideoFlow() {
        coEvery { liveStreamingRepository.stopRecordVideo() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingUseCaseImpl.stopRecordVideo()
        }
        coVerify { liveStreamingRepository.stopRecordVideo() }
    }

    @Test
    fun testStopRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRepository.stopRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingUseCaseImpl.stopRecordVideo(), result)
        }
    }

    @Test
    fun testStopRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { liveStreamingRepository.stopRecordVideo() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingUseCaseImpl.stopRecordVideo(), result)
        }
    }

    @Test
    fun testTakePhotoFlow() {
        coEvery { liveStreamingRepository.takePhoto() } returns Result.Success(Unit)
        runBlocking {
            liveStreamingUseCaseImpl.takePhoto()
        }

        coEvery { liveStreamingRepository.takePhoto() }
    }

    @Test
    fun testTakePhotoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRepository.takePhoto() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingUseCaseImpl.takePhoto(), result)
        }
    }

    @Test
    fun testTakePhotoFailed() {
        val result = Result.Error(Exception(""))
        coEvery { liveStreamingRepository.takePhoto() } returns result
        runBlocking {
            Assert.assertEquals(liveStreamingUseCaseImpl.takePhoto(), result)
        }
    }
}