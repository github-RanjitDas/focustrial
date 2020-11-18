package com.lawmobile.presentation.ui.login.pairingPhoneWithCamera

import androidx.lifecycle.MediatorLiveData
import com.lawmobile.domain.usecase.pairingPhoneWithCamera.PairingPhoneWithCameraUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.ui.login.pairingPhoneWithCamera.PairingViewModel.Companion.EXCEPTION_GET_PARAMS_TO_CONNECT
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class PairingViewModelTest {

    companion object {
        const val DEFAULT_GATEWAY_ADDRESS = "192.168.42.1"
        const val DEFAULT_SSID = "X57014694"
    }

    private val wifiHelper: WifiHelper = mockk {
        every { isEqualsValueWithSSID(DEFAULT_SSID) } returns true
        every { isEqualsValueWithSSID("X") } returns false
    }

    private val pairingPhoneWithCameraUseCase: PairingPhoneWithCameraUseCase = mockk {
        every { progressPairingCamera } returns MediatorLiveData()
    }
    private val viewModel: PairingViewModel by lazy {
        PairingViewModel(pairingPhoneWithCameraUseCase, wifiHelper)
    }

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun testGetProgressConnectionWithTheCamera() {
        every { wifiHelper.getGatewayAddress() } returns DEFAULT_GATEWAY_ADDRESS
        every { wifiHelper.getIpAddress() } returns DEFAULT_GATEWAY_ADDRESS
        coEvery {
            pairingPhoneWithCameraUseCase.loadPairingCamera(any(), any())
        } just Runs

        viewModel.getProgressConnectionWithTheCamera()
        coVerify {
            pairingPhoneWithCameraUseCase.loadPairingCamera(any(), any())
        }
    }

    @Test
    fun testGetProgressConnectionWithTheCameraErrorInIpAddress() {
        every { wifiHelper.getGatewayAddress() } returns DEFAULT_GATEWAY_ADDRESS
        every { wifiHelper.getIpAddress() } returns ""
        coEvery {
            pairingPhoneWithCameraUseCase.loadPairingCamera(any(), any())
        } just Runs

        viewModel.getProgressConnectionWithTheCamera()
        val error = viewModel.progressConnectionWithTheCamera.value as Result.Error
        Assert.assertEquals(error.exception.message, EXCEPTION_GET_PARAMS_TO_CONNECT)
    }

    @Test
    fun testGetProgressConnectionWithTheCameraErrorInGatewayAddress() {
        every { wifiHelper.getGatewayAddress() } returns ""
        every { wifiHelper.getIpAddress() } returns DEFAULT_GATEWAY_ADDRESS
        coEvery {
            pairingPhoneWithCameraUseCase.loadPairingCamera(any(), any())
        } just Runs

        viewModel.getProgressConnectionWithTheCamera()
        val error = viewModel.progressConnectionWithTheCamera.value as Result.Error
        Assert.assertEquals(error.exception.message, EXCEPTION_GET_PARAMS_TO_CONNECT)
    }

    @Test
    fun testIsValidNumberCameraBWC() {
        Assert.assertTrue(viewModel.isValidNumberCameraBWC(DEFAULT_SSID))
        Assert.assertFalse(viewModel.isValidNumberCameraBWC(""))
    }

    @Test
    fun getNetworkName() {
        every { wifiHelper.getSSIDWiFi() } returns DEFAULT_SSID
        Assert.assertEquals(DEFAULT_SSID, viewModel.getNetworkName())
    }

    @Test
    fun isWifiEnableTrue() {
        every { wifiHelper.isWifiEnable() } returns true
        Assert.assertTrue(viewModel.isWifiEnable())
    }

    @Test
    fun isWifiEnableFalse() {
        every { wifiHelper.isWifiEnable() } returns false
        Assert.assertFalse(viewModel.isWifiEnable())
    }

    @Test
    fun testIsPossibleTheConnectionSuccess() {
        every { wifiHelper.getGatewayAddress() } returns DEFAULT_GATEWAY_ADDRESS
        every { wifiHelper.getIpAddress() } returns DEFAULT_GATEWAY_ADDRESS
        coEvery { pairingPhoneWithCameraUseCase.isPossibleTheConnection(any()) } returns Result.Success(Unit)
        runBlocking { viewModel.isPossibleConnection() }
        coVerify { pairingPhoneWithCameraUseCase.isPossibleTheConnection(any()) }
    }

    @Test
    fun testIsPossibleTheConnectionErrorInGateway() {
        every { wifiHelper.getGatewayAddress() } returns ""
        viewModel.isPossibleConnection()
        val error = viewModel.validateConnectionLiveData.value as Result.Error
        Assert.assertEquals(error.exception.message, EXCEPTION_GET_PARAMS_TO_CONNECT)
    }

    @Test
    fun testResetProgress() {
        viewModel.resetProgress()
        Assert.assertTrue(viewModel.progressConnectionWithTheCamera.value == Result.Success(0))
    }
}