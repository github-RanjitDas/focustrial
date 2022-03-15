package com.lawmobile.domain.usecase.bodyWornSettings

import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.lawmobile.domain.repository.bodyWornSettings.BodyWornSettingsRepository
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BodyWornSettingsUseCaseImplTest {

    private val bodyWornSettingsRepository: BodyWornSettingsRepository = mockk()
    private val bodyWornSettingsUseCaseImpl = BodyWornSettingsUseCaseImpl(bodyWornSettingsRepository)

    @BeforeEach
    fun setup() {
        clearMocks()
    }

    @Test
    fun testChangeStatusSettingsCovertModeTrue() {
        coEvery { bodyWornSettingsRepository.changeStatusSettings(any(), any()) } returns Result.Success(Unit)
        runBlocking {
            val response = bodyWornSettingsUseCaseImpl.changeStatusSettings(
                TypesOfBodyWornSettings.CovertMode, true
            )
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { bodyWornSettingsRepository.changeStatusSettings(TypesOfBodyWornSettings.CovertMode, true) }
    }

    @Test
    fun testChangeStatusSettingsCovertModeFalse() {
        coEvery { bodyWornSettingsRepository.changeStatusSettings(any(), any()) } returns Result.Success(Unit)
        runBlocking {
            val response = bodyWornSettingsUseCaseImpl.changeStatusSettings(
                TypesOfBodyWornSettings.CovertMode, false
            )
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { bodyWornSettingsRepository.changeStatusSettings(TypesOfBodyWornSettings.CovertMode, false) }
    }

    @Test
    fun testChangeStatusSettingsBluetoothTrue() {
        coEvery { bodyWornSettingsRepository.changeStatusSettings(any(), any()) } returns Result.Success(Unit)
        runBlocking {
            val response = bodyWornSettingsUseCaseImpl.changeStatusSettings(
                TypesOfBodyWornSettings.Bluetooth, true
            )
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { bodyWornSettingsRepository.changeStatusSettings(TypesOfBodyWornSettings.Bluetooth, true) }
    }

    @Test
    fun testChangeStatusSettingsBluetoothFalse() {
        coEvery { bodyWornSettingsRepository.changeStatusSettings(any(), any()) } returns Result.Success(Unit)
        runBlocking {
            val response = bodyWornSettingsUseCaseImpl.changeStatusSettings(
                TypesOfBodyWornSettings.Bluetooth, false
            )
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { bodyWornSettingsRepository.changeStatusSettings(TypesOfBodyWornSettings.Bluetooth, false) }
    }

    @Test
    fun testGetParametersEnableSuccess() {
        coEvery { bodyWornSettingsRepository.getParametersEnable() } returns Result.Success(mockk(relaxed = true))
        runBlocking {
            val response = bodyWornSettingsUseCaseImpl.getParametersEnable()
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { bodyWornSettingsRepository.getParametersEnable() }
    }

    @Test
    fun testGetParametersEnableError() {
        coEvery { bodyWornSettingsRepository.getParametersEnable() } returns Result.Error(Exception(""))
        runBlocking {
            val response = bodyWornSettingsUseCaseImpl.getParametersEnable()
            Assert.assertTrue(response is Result.Error)
        }
        coVerify { bodyWornSettingsRepository.getParametersEnable() }
    }
}
