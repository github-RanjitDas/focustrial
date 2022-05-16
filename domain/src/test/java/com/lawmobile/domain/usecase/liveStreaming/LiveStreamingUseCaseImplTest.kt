package com.lawmobile.domain.usecase.liveStreaming

import com.lawmobile.domain.repository.liveStreaming.LiveStreamingRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

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
    fun testGetUrlForLiveStreamVerifyValueInURL() {
        every { liveStreamingRepository.getUrlForLiveStream() } returns "xyz"
        val url = liveStreamingUseCaseImpl.getUrlForLiveStream()
        Assert.assertEquals(url, "xyz")
    }

    @Test
    fun testGetUrlForLiveStreamVerifyValueEmpty() {
        every { liveStreamingRepository.getUrlForLiveStream() } returns ""
        val url = liveStreamingUseCaseImpl.getUrlForLiveStream()
        Assert.assertEquals(url, "")
    }

    @Test
    fun testStartRecordVideoFlow() {
        coEvery { liveStreamingRepository.startRecordVideo() } returns Result.Success(Unit)
        runBlocking { liveStreamingUseCaseImpl.startRecordVideo() }
        coVerify { liveStreamingRepository.startRecordVideo() }
    }

    @Test
    fun testStartRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRepository.startRecordVideo() } returns result
        runBlocking { Assert.assertEquals(liveStreamingUseCaseImpl.startRecordVideo(), result) }
    }

    @Test
    fun testStartRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { liveStreamingRepository.startRecordVideo() } returns result
        runBlocking { Assert.assertEquals(liveStreamingUseCaseImpl.startRecordVideo(), result) }
    }

    @Test
    fun testStopRecordVideoFlow() {
        coEvery { liveStreamingRepository.stopRecordVideo() } returns Result.Success(Unit)
        runBlocking { liveStreamingUseCaseImpl.stopRecordVideo() }
        coVerify { liveStreamingRepository.stopRecordVideo() }
    }

    @Test
    fun testStopRecordVideoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRepository.stopRecordVideo() } returns result
        runBlocking { Assert.assertEquals(liveStreamingUseCaseImpl.stopRecordVideo(), result) }
    }

    @Test
    fun testStopRecordVideoFailed() {
        val result = Result.Error(Exception("Message"))
        coEvery { liveStreamingRepository.stopRecordVideo() } returns result
        runBlocking { Assert.assertEquals(liveStreamingUseCaseImpl.stopRecordVideo(), result) }
    }

    @Test
    fun testTakePhotoFlow() {
        coEvery { liveStreamingRepository.takePhoto() } returns Result.Success(Unit)
        runBlocking { liveStreamingUseCaseImpl.takePhoto() }
        coEvery { liveStreamingRepository.takePhoto() }
    }

    @Test
    fun testTakePhotoSuccess() {
        val result = Result.Success(Unit)
        coEvery { liveStreamingRepository.takePhoto() } returns result
        runBlocking { Assert.assertEquals(liveStreamingUseCaseImpl.takePhoto(), result) }
    }

    @Test
    fun testTakePhotoFailed() {
        val result = Result.Error(Exception(""))
        coEvery { liveStreamingRepository.takePhoto() } returns result
        runBlocking { Assert.assertEquals(liveStreamingUseCaseImpl.takePhoto(), result) }
    }

    @Test
    fun testGetCatalogInfoSuccess() {
        coEvery { liveStreamingRepository.getCatalogInfo() } returns Result.Success(mockk())
        runBlocking {
            val result = liveStreamingUseCaseImpl.getCatalogInfo()
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { liveStreamingRepository.getCatalogInfo() }
    }

    @Test
    fun testGetCatalogInfoError() {
        coEvery { liveStreamingRepository.getCatalogInfo() } returns Result.Error(mockk())
        runBlocking {
            val result = liveStreamingUseCaseImpl.getCatalogInfo()
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { liveStreamingRepository.getCatalogInfo() }
    }

    @Test
    fun testGetBatteryLevel() {
        val result = Result.Success(10)
        coEvery { liveStreamingRepository.getBatteryLevel() } returns result
        runBlocking {
            val response = liveStreamingUseCaseImpl.getBatteryLevel()
            Assert.assertEquals(response, result)
        }
        coVerify { liveStreamingRepository.getBatteryLevel() }
    }

    @Test
    fun testGetFreeStorage() {
        val result = Result.Success("10000")
        coEvery { liveStreamingRepository.getFreeStorage() } returns result
        runBlocking {
            val response = liveStreamingUseCaseImpl.getFreeStorage()
            Assert.assertEquals(response, result)
        }
        coVerify { liveStreamingRepository.getFreeStorage() }
    }

    @Test
    fun testGetTotalStorage() {
        val result = Result.Success("10000")
        coEvery { liveStreamingRepository.getTotalStorage() } returns result
        runBlocking {
            val response = liveStreamingUseCaseImpl.getTotalStorage()
            Assert.assertEquals(response, result)
        }
        coVerify { liveStreamingRepository.getTotalStorage() }
    }

    @Test
    fun disconnectCamera() {
        coEvery { liveStreamingRepository.disconnectCamera() } returns Result.Success(Unit)
        runBlocking { liveStreamingUseCaseImpl.disconnectCamera() }
        coVerify { liveStreamingRepository.disconnectCamera() }
    }

    @Test
    fun testIsFolderOnCameraTrue() {
        coEvery { liveStreamingRepository.isFolderOnCamera(any()) } returns true
        runBlocking { liveStreamingUseCaseImpl.isFolderOnCamera("SAFE") }
        coEvery { liveStreamingRepository.isFolderOnCamera(any()) }
    }

    @Test
    fun testIsFolderOnCameraFalse() {
        coEvery { liveStreamingRepository.isFolderOnCamera(any()) } returns false
        runBlocking { liveStreamingUseCaseImpl.isFolderOnCamera("SAFE") }
        coEvery { liveStreamingRepository.isFolderOnCamera(any()) }
    }
}
