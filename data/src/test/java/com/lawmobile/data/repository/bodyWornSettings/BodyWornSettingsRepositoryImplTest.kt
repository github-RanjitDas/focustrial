package com.lawmobile.data.repository.bodyWornSettings

import com.lawmobile.data.datasource.remote.bodyWornSettings.BodyWornSettingsDataSource
import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BodyWornSettingsRepositoryImplTest {

    private val bodyWornSettingsDataSource: BodyWornSettingsDataSource = mockk()
    private val bodyWornSettingsRepositoryImpl = BodyWornSettingsRepositoryImpl(bodyWornSettingsDataSource)

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun testChangeStatusSettingsCovertModeTrue() {
        coEvery { bodyWornSettingsDataSource.changeStatusSettings(any(), any()) } returns Result.Success(Unit)
        runBlocking {
            val response = bodyWornSettingsRepositoryImpl.changeStatusSettings(TypesOfBodyWornSettings.CovertMode, true)
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { bodyWornSettingsDataSource.changeStatusSettings(TypesOfBodyWornSettings.CovertMode, true) }
    }

    @Test
    fun testChangeStatusSettingsCovertModeFalse() {
        coEvery { bodyWornSettingsDataSource.changeStatusSettings(any(), any()) } returns Result.Success(Unit)
        runBlocking {
            val response = bodyWornSettingsRepositoryImpl.changeStatusSettings(TypesOfBodyWornSettings.CovertMode, false)
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { bodyWornSettingsDataSource.changeStatusSettings(TypesOfBodyWornSettings.CovertMode, false) }
    }

    @Test
    fun testChangeStatusSettingsBluetoothTrue() {
        coEvery { bodyWornSettingsDataSource.changeStatusSettings(any(), any()) } returns Result.Success(Unit)
        runBlocking {
            val response = bodyWornSettingsRepositoryImpl.changeStatusSettings(TypesOfBodyWornSettings.Bluetooth, true)
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { bodyWornSettingsDataSource.changeStatusSettings(TypesOfBodyWornSettings.Bluetooth, true) }
    }

    @Test
    fun testChangeStatusSettingsBluetoothFalse() {
        coEvery { bodyWornSettingsDataSource.changeStatusSettings(any(), any()) } returns Result.Success(Unit)
        runBlocking {
            val response = bodyWornSettingsRepositoryImpl.changeStatusSettings(TypesOfBodyWornSettings.Bluetooth, false)
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { bodyWornSettingsDataSource.changeStatusSettings(TypesOfBodyWornSettings.Bluetooth, false) }
    }

    @Test
    fun testGetParametersEnableSuccess() {
        coEvery { bodyWornSettingsDataSource.getParametersEnable() } returns Result.Success(mockk(relaxed = true))
        runBlocking {
            val response = bodyWornSettingsRepositoryImpl.getParametersEnable()
            Assert.assertTrue(response is Result.Success)
        }
        coVerify { bodyWornSettingsDataSource.getParametersEnable() }
    }

    @Test
    fun testGetParametersEnableError() {
        coEvery { bodyWornSettingsDataSource.getParametersEnable() } returns Result.Error(Exception(""))
        runBlocking {
            val response = bodyWornSettingsRepositoryImpl.getParametersEnable()
            Assert.assertTrue(response is Result.Error)
        }
        coVerify { bodyWornSettingsDataSource.getParametersEnable() }
    }
}
