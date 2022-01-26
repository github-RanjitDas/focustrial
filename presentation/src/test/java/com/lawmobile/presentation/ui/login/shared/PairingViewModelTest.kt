package com.lawmobile.presentation.ui.login.shared

import com.lawmobile.domain.enums.CameraType
import com.lawmobile.domain.usecase.pairingPhoneWithCamera.PairingPhoneWithCameraUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.ui.login.shared.PairingViewModel
import com.lawmobile.presentation.ui.login.shared.PairingViewModel.Companion.EXCEPTION_GET_PARAMS_TO_CONNECT
import com.safefleet.mobile.kotlin_commons.helpers.Result
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
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

    private val pairingPhoneWithCameraUseCase: PairingPhoneWithCameraUseCase = mockk()
    private val viewModel: PairingViewModel by lazy {
        PairingViewModel(pairingPhoneWithCameraUseCase, wifiHelper)
    }

    private val dispatcher = TestCoroutineDispatcher()

    @ExperimentalCoroutinesApi
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @Test
    fun testGetProgressConnectionWithTheCamera() {
        every { wifiHelper.getGatewayAddress() } returns DEFAULT_GATEWAY_ADDRESS
        every { wifiHelper.getIpAddress() } returns DEFAULT_GATEWAY_ADDRESS
        coEvery {
            pairingPhoneWithCameraUseCase.loadPairingCamera(any(), any(), any())
        } just Runs

        viewModel.connectWithCamera()

        coVerify {
            pairingPhoneWithCameraUseCase.loadPairingCamera(any(), any(), any())
        }
    }

    @Test
    fun testGetProgressConnectionWithTheCameraErrorInIpAddress() {
        every { wifiHelper.getGatewayAddress() } returns DEFAULT_GATEWAY_ADDRESS
        every { wifiHelper.getIpAddress() } returns ""
        coEvery {
            pairingPhoneWithCameraUseCase.loadPairingCamera(any(), any(), any())
        } just Runs

        viewModel.connectWithCamera()

        val result = (viewModel.connectionProgress.value as Result.Error).exception.message
        Assert.assertEquals(result, EXCEPTION_GET_PARAMS_TO_CONNECT)
    }

    @Test
    fun testGetProgressConnectionWithTheCameraErrorInGatewayAddress() {
        every { wifiHelper.getGatewayAddress() } returns ""
        every { wifiHelper.getIpAddress() } returns DEFAULT_GATEWAY_ADDRESS

        coEvery {
            pairingPhoneWithCameraUseCase.loadPairingCamera(any(), any(), any())
        } just Runs

        viewModel.connectWithCamera()

        val result = (viewModel.connectionProgress.value as Result.Error).exception.message
        Assert.assertEquals(result, EXCEPTION_GET_PARAMS_TO_CONNECT)
    }

    @Test
    fun testIsValidNumberCameraBWC() {
        Assert.assertTrue(CameraType.isValidBodyCameraNumber(DEFAULT_SSID))
        Assert.assertFalse(CameraType.isValidBodyCameraNumber(""))
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
        coEvery { pairingPhoneWithCameraUseCase.isPossibleTheConnection(any()) } returns Result.Success(
            Unit
        )
        runBlocking { viewModel.isConnectionPossible() }
        coVerify { pairingPhoneWithCameraUseCase.isPossibleTheConnection(any()) }
    }

    @Test
    fun testIsPossibleTheConnectionErrorInGateway() {
        every { wifiHelper.getGatewayAddress() } returns ""
        viewModel.isConnectionPossible()
        val error = viewModel.isConnectionPossible.value as Result.Error
        Assert.assertEquals(error.exception.message, EXCEPTION_GET_PARAMS_TO_CONNECT)
    }

    @Test
    fun testCleanCacheFiles() {
        every { viewModel.cleanCacheFiles() } just runs
        viewModel.cleanCacheFiles()
    }

    @Test
    fun testResetProgress() {
        viewModel.resetProgress()
        Assert.assertTrue(viewModel.connectionProgress.value == Result.Success(0))
    }

    @Test
    fun suggestWiFiNetwork() {
        every { wifiHelper.suggestWiFiNetwork(any(), any(), any()) } returns Unit
        viewModel.suggestWiFiNetwork("", "") {}
        verify { wifiHelper.suggestWiFiNetwork(any(), any(), any()) }
    }
}
