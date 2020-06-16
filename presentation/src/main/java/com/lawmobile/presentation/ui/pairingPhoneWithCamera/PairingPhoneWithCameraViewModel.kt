package com.lawmobile.presentation.ui.pairingPhoneWithCamera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.pairingPhoneWithCamera.PairingPhoneWithCameraUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.WifiConnection
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class PairingPhoneWithCameraViewModel @Inject constructor(
    private val pairingPhoneWithCameraUseCase: PairingPhoneWithCameraUseCase,
    private val wifiHelper: WifiHelper,
    private val wifiConnection: WifiConnection
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

    fun getSSIDSavedIfExist(): Result<String> {
        return pairingPhoneWithCameraUseCase.getSSIDSavedIfExist()
    }

    fun saveSerialNumberOfCamera(serialNumber: String) {
        pairingPhoneWithCameraUseCase.saveSerialNumberOfCamera(serialNumber)
    }

    fun isValidNumberCameraBWC(codeCamera: String): Boolean =
        wifiHelper.isEqualsValueWithSSID("X$codeCamera")

    fun connectCellPhoneToWifiCamera(
        codeCamera: String,
        isConnectedSuccess: (connected: Boolean) -> Unit
    ) {
        wifiConnection.connectionWithHotspotCamera("X$codeCamera", isConnectedSuccess)
    }

    fun isWifiEnable(): Boolean  = wifiConnection.isWifiEnable()

    companion object {
        const val EXCEPTION_GET_PARAMS_TO_CONNECT =
            "Exception in get params to configure connection"
    }

}