package com.lawmobile.domain.usecase.pairingPhoneWithCamera

import androidx.lifecycle.LiveData
import com.lawmobile.domain.repository.pairingPhoneWithCamera.PairingPhoneWithCameraRepository
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PairingPhoneWithCameraUseCaseImplTest {

    private val progressCamera: LiveData<Result<Int>> = mockk()
    private val pairingPhoneWithCameraRepository: PairingPhoneWithCameraRepository = mockk {
        coEvery { loadPairingCamera(any(), any()) } just Runs
        coEvery { progressPairingCamera } returns progressCamera
    }

    private val useCase: PairingPhoneWithCameraUseCaseImpl by lazy {
        PairingPhoneWithCameraUseCaseImpl(
            pairingPhoneWithCameraRepository
        )
    }

    @Test
    fun testGetProgressConnectionWithTheCamera() {
        runBlocking {
            useCase.loadPairingCamera("", "")
        }

        coVerify {
            pairingPhoneWithCameraRepository.loadPairingCamera("", "")
        }
    }

    @Test
    fun testToCheckPairingLiveData() {
        Assert.assertEquals(useCase.progressPairingCamera, progressCamera)
    }

    @Test
    fun testIsPossibleTheConnection() {
        coEvery { pairingPhoneWithCameraRepository.isPossibleTheConnection(any()) } returns Result.Success(Unit)
        runBlocking { useCase.isPossibleTheConnection("10.10.10.4") }
        coVerify { pairingPhoneWithCameraRepository.isPossibleTheConnection("10.10.10.4") }
    }
}