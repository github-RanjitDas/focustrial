package com.lawmobile.domain.usecase.audioDetail

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.repository.audioDetail.AudioDetailRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test

class AudioDetailUseCaseImplTest {

    private val audioDetailRepository: AudioDetailRepository = mockk()
    private val audioDetailUseCaseImpl by lazy {
        AudioDetailUseCaseImpl(audioDetailRepository)
    }

    @Test
    fun testGetAudioBytesSuccess() {
        val domainCameraFile: DomainCameraFile = mockk()
        val byte = ByteArray(1)
        coEvery { audioDetailRepository.getAudioBytes(domainCameraFile) } returns Result.Success(
            byte
        )
        runBlocking {
            val result = audioDetailUseCaseImpl.getAudioBytes(domainCameraFile)
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { audioDetailRepository.getAudioBytes(domainCameraFile) }
    }

    @Test
    fun testGetAudioBytesError() {
        val domainCameraFile: DomainCameraFile = mockk()
        coEvery { audioDetailRepository.getAudioBytes(domainCameraFile) } returns Result.Error(
            mockk()
        )
        runBlocking {
            val result = audioDetailUseCaseImpl.getAudioBytes(domainCameraFile)
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { audioDetailRepository.getAudioBytes(domainCameraFile) }
    }

    @Test
    fun testSavePartnerIdAudioFlow() {
        coEvery {
            audioDetailRepository.saveAudioPartnerId(
                any(),
                "partnerId"
            )
        } returns Result.Success(Unit)
        runBlocking {
            val result = audioDetailUseCaseImpl.savePartnerIdAudio(mockk(), "partnerId")
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { audioDetailRepository.saveAudioPartnerId(any(), "partnerId") }
    }

    @Test
    fun testGetInformationOfAudioSuccess() {
        coEvery { audioDetailRepository.getInformationOfAudio(any()) } returns Result.Success(
            mockk()
        )
        runBlocking {
            val result = audioDetailUseCaseImpl.getInformationOfAudio(mockk())
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { audioDetailRepository.getInformationOfAudio(any()) }
    }

    @Test
    fun testGetInformationOfAudioError() {
        coEvery {
            audioDetailRepository.getInformationOfAudio(any())
        } returns Result.Error(Exception())
        runBlocking {
            val result = audioDetailUseCaseImpl.getInformationOfAudio(mockk())
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { audioDetailRepository.getInformationOfAudio(any()) }
    }

    @Test
    fun testGetAssociatedVideosSuccess() {
        coEvery { audioDetailRepository.getAssociatedVideos(any()) } returns Result.Success(mockk())
        runBlocking {
            val result = audioDetailUseCaseImpl.getAssociatedVideos(mockk())
            Assert.assertTrue(result is Result.Success)
        }
        coVerify { audioDetailRepository.getAssociatedVideos(any()) }
    }

    @Test
    fun testGetAssociatedVideosError() {
        coEvery { audioDetailRepository.getAssociatedVideos(any()) } returns Result.Error(Exception())
        runBlocking {
            val result = audioDetailUseCaseImpl.getAssociatedVideos(mockk())
            Assert.assertTrue(result is Result.Error)
        }
        coVerify { audioDetailRepository.getAssociatedVideos(any()) }
    }
}
