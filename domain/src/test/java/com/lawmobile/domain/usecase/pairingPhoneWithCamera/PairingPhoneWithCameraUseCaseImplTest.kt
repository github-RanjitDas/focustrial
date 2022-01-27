package com.lawmobile.domain.usecase.pairingPhoneWithCamera

import com.lawmobile.domain.repository.pairingPhoneWithCamera.PairingPhoneWithCameraRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PairingPhoneWithCameraUseCaseImplTest {

    private val progressPairingCamera: ((Result<Int>) -> Unit) = { }
    private val repository: PairingPhoneWithCameraRepository = mockk()
    private val useCase: PairingPhoneWithCameraUseCaseImpl by lazy {
        PairingPhoneWithCameraUseCaseImpl(repository)
    }

    @Test
    fun testGetProgressConnectionWithTheCamera() {
        coEvery { repository.loadPairingCamera(any(), any(), any()) } just Runs
        runBlocking { useCase.loadPairingCamera("", "", progressPairingCamera) }
        coVerify { repository.loadPairingCamera("", "", progressPairingCamera) }
    }

    @Test
    fun testIsPossibleTheConnection() {
        coEvery { repository.isPossibleTheConnection(any()) } returns Result.Success(Unit)
        runBlocking { useCase.isPossibleTheConnection("10.10.10.4") }
        coVerify { repository.isPossibleTheConnection("10.10.10.4") }
    }

    @Test
    fun cleanCacheFiles() {
        every { repository.cleanCacheFiles() } just Runs
        useCase.cleanCacheFiles()
        verify { repository.cleanCacheFiles() }
    }
}
