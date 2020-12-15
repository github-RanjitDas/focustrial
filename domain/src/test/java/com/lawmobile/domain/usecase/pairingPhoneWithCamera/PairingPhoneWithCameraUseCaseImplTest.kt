package com.lawmobile.domain.usecase.pairingPhoneWithCamera

import com.lawmobile.domain.repository.pairingPhoneWithCamera.PairingPhoneWithCameraRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PairingPhoneWithCameraUseCaseImplTest {

    private val progressPairingCamera: ((Result<Int>) -> Unit) = { }
    private val pairingPhoneWithCameraRepository: PairingPhoneWithCameraRepository = mockk {
        coEvery { loadPairingCamera(any(), any(), any()) } just Runs
    }

    private val useCase: PairingPhoneWithCameraUseCaseImpl by lazy {
        PairingPhoneWithCameraUseCaseImpl(
            pairingPhoneWithCameraRepository
        )
    }

    @Test
    fun testGetProgressConnectionWithTheCamera() {
        runBlocking {
            useCase.loadPairingCamera("", "", progressPairingCamera)
        }

        coVerify {
            pairingPhoneWithCameraRepository.loadPairingCamera("", "", progressPairingCamera)
        }
    }

    @Test
    fun testIsPossibleTheConnection() {
        coEvery { pairingPhoneWithCameraRepository.isPossibleTheConnection(any()) } returns Result.Success(Unit)
        runBlocking { useCase.isPossibleTheConnection("10.10.10.4") }
        coVerify { pairingPhoneWithCameraRepository.isPossibleTheConnection("10.10.10.4") }
    }
}