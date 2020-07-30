package com.lawmobile.presentation.ui.login.pairingPhoneWithCamera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.pairingPhoneWithCamera.PairingPhoneWithCameraUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class PairingViewModel @Inject constructor(
    private val pairingPhoneWithCameraUseCase: PairingPhoneWithCameraUseCase,
    private val wifiHelper: WifiHelper
) :
    BaseViewModel() {

    val progressConnectionWithTheCamera: MutableLiveData<Result<Int>> =
        pairingPhoneWithCameraUseCase.progressPairingCamera as MutableLiveData<Result<Int>>

    fun getProgressConnectionWithTheCamera() {
        val gateway = wifiHelper.getGatewayAddress()
        val ipAddress = wifiHelper.getIpAddress()
        if (ipAddress.isEmpty() || gateway.isEmpty()) {
            progressConnectionWithTheCamera.postValue(
                Result.Error(Exception(EXCEPTION_GET_PARAMS_TO_CONNECT))
            )
        }

        viewModelScope.launch {
            pairingPhoneWithCameraUseCase.loadPairingCamera(gateway, ipAddress)
        }
    }

    fun isValidNumberCameraBWC(codeCamera: String): Boolean =
        codeCamera.contains(CAMERA_SSID_IDENTIFIER)

    fun getNetworkName() = wifiHelper.getSSIDWiFi()

    fun isWifiEnable(): Boolean = wifiHelper.isWifiEnable()

    companion object {
        const val EXCEPTION_GET_PARAMS_TO_CONNECT =
            "Exception in get params to configure connection"
        const val CAMERA_SSID_IDENTIFIER = "X57"
    }

}