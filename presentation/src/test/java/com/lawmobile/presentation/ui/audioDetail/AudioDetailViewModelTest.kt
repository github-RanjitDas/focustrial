package com.lawmobile.presentation.ui.audioDetail

import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.usecase.audioDetail.AudioDetailUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class AudioDetailViewModelTest {

    private val audioDetailUseCase: AudioDetailUseCase = mockk()

    private val audioDetailViewModel by lazy {
        AudioDetailViewModel(audioDetailUseCase)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetAudioBytesSuccess() = runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk()
        val byte = ByteArray(1)

        coEvery { audioDetailUseCase.getAudioBytes(any()) } returns Result.Success(byte)

        audioDetailViewModel.getAudioBytes(domainCameraFile)
        val response = audioDetailViewModel.audioBytesResult.value
        Assert.assertTrue(response is Result.Success)

        coVerify { audioDetailUseCase.getAudioBytes(any()) }
    }

    @Test
    fun testGetAudioBytesError() = runBlockingTest {
        val domainCameraFile: DomainCameraFile = mockk()

        coEvery { audioDetailUseCase.getAudioBytes(any()) } returns Result.Error(mockk())

        audioDetailViewModel.getAudioBytes(domainCameraFile)
        dispatcher.advanceTimeBy(1000)
        val response = audioDetailViewModel.audioBytesResult.value
        Assert.assertTrue(response is Result.Error)

        coVerify { audioDetailUseCase.getAudioBytes(any()) }
    }

    @Test
    fun testSavePartnerIdSuccess() = runBlockingTest {
        coEvery {
            audioDetailUseCase.savePartnerIdAudio(any(), any())
        } returns Result.Success(Unit)

        audioDetailViewModel.savePartnerId(mockk(relaxed = true), "partnerId")
        val valueLiveData = audioDetailViewModel.savePartnerIdResult.value
        Assert.assertTrue(valueLiveData is Result.Success)

        coVerify { audioDetailUseCase.savePartnerIdAudio(any(), any()) }
    }

    @Test
    fun testSavePartnerIdError() = runBlockingTest {
        coEvery {
            audioDetailUseCase.savePartnerIdAudio(any(), any())
        } returns Result.Error(mockk())

        audioDetailViewModel.savePartnerId(mockk(relaxed = true), "partnerId")
        val valueLiveData = audioDetailViewModel.savePartnerIdResult.value
        Assert.assertTrue(valueLiveData is Result.Error)
    }

    @Test
    fun testGetInformationAudioMetadataSuccess() = runBlockingTest {
        coEvery {
            audioDetailUseCase.getInformationOfAudio(any())
        } returns Result.Success(mockk(relaxed = true))

        audioDetailViewModel.getAudioInformation(mockk())
        val valueLiveData = audioDetailViewModel.audioInformationResult.value
        Assert.assertTrue(valueLiveData is Result.Success)

        coVerify { audioDetailUseCase.getInformationOfAudio(any()) }
    }

    @Test
    fun testGetInformationAudioMetadataError() = runBlockingTest {
        coEvery { audioDetailUseCase.getInformationOfAudio(any()) } returns Result.Error(mockk())
        audioDetailViewModel.getAudioInformation(mockk())
        dispatcher.advanceTimeBy(5100)
        val valueLiveData = audioDetailViewModel.audioInformationResult.value
        Assert.assertTrue(valueLiveData is Result.Error)
    }

    @Test
    fun testGetAssociatedVideosSuccess() {
        coEvery {
            audioDetailUseCase.getAssociatedVideos(any())
        } returns Result.Success(mockk(relaxed = true))

        audioDetailViewModel.getAssociatedVideos(mockk())
        val valueLiveData = audioDetailViewModel.associatedVideosResult.value

        Assert.assertTrue(valueLiveData is Result.Success)
        coVerify { audioDetailUseCase.getAssociatedVideos(any()) }
    }

    @Test
    fun testGetAssociatedVideosError() {
        coEvery { audioDetailUseCase.getAssociatedVideos(any()) } returns Result.Error(Exception())

        audioDetailViewModel.getAssociatedVideos(mockk())
        val valueLiveData = audioDetailViewModel.associatedVideosResult.value

        Assert.assertTrue(valueLiveData is Result.Error)
        coVerify { audioDetailUseCase.getAssociatedVideos(any()) }
    }
}
