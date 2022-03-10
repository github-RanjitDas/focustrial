package com.lawmobile.data.datasource.remote.audioDetail

import com.lawmobile.body_cameras.CameraService
import com.lawmobile.body_cameras.entities.CameraFile
import com.lawmobile.data.utils.CameraServiceFactory
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AudioDetailRemoteDataSourceImplTest {

    private val cameraService: CameraService = mockk()
    private val cameraServiceFactory: CameraServiceFactory = mockk {
        every { create() } returns cameraService
    }
    private val audioDetailRemoteDataSourceImpl by lazy {
        AudioDetailRemoteDataSourceImpl(cameraServiceFactory)
    }

    @Test
    fun testGetAudioBytesSuccess() {
        val cameraFile: CameraFile = mockk()
        val byte = ByteArray(1)

        coEvery { cameraService.getAudioBytes(cameraFile) } returns Result.Success(byte)
        runBlocking {
            val result = audioDetailRemoteDataSourceImpl.getAudioBytes(cameraFile)
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.getAudioBytes(cameraFile) }
    }

    @Test
    fun testGetAudioBytesError() {
        val connectFile: CameraFile = mockk()
        coEvery { cameraService.getAudioBytes(connectFile) } returns Result.Error(mockk())
        runBlocking {
            val result = audioDetailRemoteDataSourceImpl.getAudioBytes(connectFile)
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { cameraService.getAudioBytes(connectFile) }
    }

    @Test
    fun testSavePartnerIdAudioFlow() {
        coEvery { cameraService.saveAudioMetadata(any()) } returns Result.Success(Unit)
        runBlocking {
            val result = audioDetailRemoteDataSourceImpl.savePartnerIdAudio(mockk())
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.saveAudioMetadata(any()) }
    }

    @Test
    fun testGetInformationOfAudioSuccess() {
        coEvery { cameraService.getAudioMetadata(any()) } returns Result.Success(mockk())
        runBlocking {
            val result = audioDetailRemoteDataSourceImpl.getInformationOfAudio(mockk())
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.getAudioMetadata(any()) }
    }

    @Test
    fun testGetInformationOfAudioError() {
        coEvery { cameraService.getAudioMetadata(any()) } returns Result.Error(Exception())
        runBlocking {
            val result = audioDetailRemoteDataSourceImpl.getInformationOfAudio(mockk())
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { cameraService.getAudioMetadata(any()) }
    }

    @Test
    fun testGetAssociatedVideosSuccess() {
        coEvery { cameraService.getAssociatedVideos(any()) } returns Result.Success(mockk())
        runBlocking {
            val result = audioDetailRemoteDataSourceImpl.getAssociatedVideos(mockk())
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { cameraService.getAssociatedVideos(any()) }
    }

    @Test
    fun testGetAssociatedVideosError() {
        coEvery { cameraService.getAssociatedVideos(any()) } returns Result.Error(Exception())
        runBlocking {
            val result = audioDetailRemoteDataSourceImpl.getAssociatedVideos(mockk())
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { cameraService.getAssociatedVideos(any()) }
    }
}
