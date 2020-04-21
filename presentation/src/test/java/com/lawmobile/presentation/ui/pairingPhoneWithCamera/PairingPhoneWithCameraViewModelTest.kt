package com.lawmobile.presentation.ui.pairingPhoneWithCamera

import androidx.lifecycle.MediatorLiveData
import com.lawmobile.domain.usecase.pairingPhoneWithCamera.PairingPhoneWithCameraUseCase
import com.lawmobile.presentation.InstantExecutorExtension
import com.lawmobile.presentation.ui.pairingPhoneWithCamera.PairingPhoneWithCameraViewModel.Companion.EXCEPTION_GET_PARAMS_TO_CONNECT
import com.lawmobile.presentation.utils.WifiConnection
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.mobile.commons.helpers.Result
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(InstantExecutorExtension::class)
class PairingPhoneWithCameraViewModelTest {

    companion object {
        const val DEFAULT_GATEWAY_ADDRESS = "192.168.42.1"
        const val DEFAULT_SSID = "X57014694"
        const val DEFAULT_SERIAL_NUMBER = "57014694"
    }


    private val wifiHelper: WifiHelper = mockk {
        every { isEqualsValueWithSSID(DEFAULT_SSID) } returns true
        every { isEqualsValueWithSSID("X") } returns false
    }

    private val wifiConnection: WifiConnection = mockk {
        every { connectionWithHotspotCamera(DEFAULT_SSID, any()) } answers {
            secondArg<(data: Boolean) -> Unit>().invoke(true)
        }
        every { connectionWithHotspotCamera("X", any()) } answers {
            secondArg<(data: Boolean) -> Unit>().invoke(false)
        }

    }

    private val pairingPhoneWithCameraUseCase: PairingPhoneWithCameraUseCase = mockk{
        every { progressPairingCamera } returns MediatorLiveData()
    }
    private val viewModel: PairingPhoneWithCameraViewModel by lazy {
        PairingPhoneWithCameraViewModel(pairingPhoneWithCameraUseCase, wifiHelper, wifiConnection)
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
        Assert.assertTrue(viewModel.isValidNumberCameraBWC(DEFAULT_SERIAL_NUMBER))
        Assert.assertFalse(viewModel.isValidNumberCameraBWC(""))
    }

    @Test
    fun testConnectCellPhoneToWifiCamera() {
        viewModel.connectCellPhoneToWifiCamera(DEFAULT_SERIAL_NUMBER) {
            Assert.assertTrue(it)
        }

        viewModel.connectCellPhoneToWifiCamera("") {
            Assert.assertFalse(it)
        }
    }

    @Test
    fun testGetSSIDSavedIfExist(){
        every { pairingPhoneWithCameraUseCase.getSSIDSavedIfExist() } returns Result.Success("123456789")
        viewModel.getSSIDSavedIfExist()
        verify { pairingPhoneWithCameraUseCase.getSSIDSavedIfExist() }
    }

    @Test
    fun testSaveSerialNumberOfCamera(){
        every { pairingPhoneWithCameraUseCase.saveSerialNumberOfCamera(any()) } just Runs
        viewModel.saveSerialNumberOfCamera("123")
        verify { pairingPhoneWithCameraUseCase.saveSerialNumberOfCamera("123") }
    }
}