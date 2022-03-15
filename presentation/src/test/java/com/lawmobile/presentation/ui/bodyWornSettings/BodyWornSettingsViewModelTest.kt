package com.lawmobile.presentation.ui.bodyWornSettings

import com.lawmobile.domain.enums.TypesOfBodyWornSettings
import com.lawmobile.domain.usecase.bodyWornSettings.BodyWornSettingsUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class BodyWornSettingsViewModelTest {

    private val useCaseSettings: BodyWornSettingsUseCase = mockk()
    private val viewModel by lazy {
        BodyWornSettingsViewModel(useCaseSettings)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetBodyWornSettingsSuccess() {
        coEvery { useCaseSettings.getParametersEnable() } returns Result.Success(mockk(relaxed = true))
        viewModel.getBodyWornSettings()
        val response = viewModel.bodyWornSettingsLiveData.value
        Assert.assertTrue(response is Result.Success)
        coVerify { useCaseSettings.getParametersEnable() }
    }

    @Test
    fun testGetBodyWornSettingsError() {
        coEvery { useCaseSettings.getParametersEnable() } returns Result.Error(Exception(""))
        viewModel.getBodyWornSettings()
        val response = viewModel.bodyWornSettingsLiveData.value
        Assert.assertTrue(response is Result.Error)
        coVerify { useCaseSettings.getParametersEnable() }
    }

    @Test
    fun testChangeBodyWornSettingCovertModeTrue() {
        coEvery { useCaseSettings.changeStatusSettings(any(), any()) } returns Result.Success(Unit)
        viewModel.changeBodyWornSetting(TypesOfBodyWornSettings.CovertMode, true)
        val response = viewModel.changeStatusSettingLiveData.value
        Assert.assertTrue(response is Result.Success)
        coVerify { useCaseSettings.changeStatusSettings(TypesOfBodyWornSettings.CovertMode, true) }
    }

    @Test
    fun testChangeBodyWornSettingCovertModeFalse() {
        coEvery { useCaseSettings.changeStatusSettings(any(), any()) } returns Result.Success(Unit)
        viewModel.changeBodyWornSetting(TypesOfBodyWornSettings.CovertMode, false)
        val response = viewModel.changeStatusSettingLiveData.value
        Assert.assertTrue(response is Result.Success)
        coVerify { useCaseSettings.changeStatusSettings(TypesOfBodyWornSettings.CovertMode, false) }
    }

    @Test
    fun testChangeBodyWornSettingBluetoothTrue() {
        coEvery { useCaseSettings.changeStatusSettings(any(), any()) } returns Result.Success(Unit)
        viewModel.changeBodyWornSetting(TypesOfBodyWornSettings.Bluetooth, true)
        val response = viewModel.changeStatusSettingLiveData.value
        Assert.assertTrue(response is Result.Success)
        coVerify { useCaseSettings.changeStatusSettings(TypesOfBodyWornSettings.Bluetooth, true) }
    }

    @Test
    fun testChangeBodyWornSettingError() {
        coEvery { useCaseSettings.changeStatusSettings(any(), any()) } returns Result.Error(
            Exception("")
        )
        viewModel.changeBodyWornSetting(TypesOfBodyWornSettings.CovertMode, true)
        val response = viewModel.changeStatusSettingLiveData.value
        Assert.assertTrue(response is Result.Error)
        coVerify { useCaseSettings.changeStatusSettings(any(), any()) }
    }
}
