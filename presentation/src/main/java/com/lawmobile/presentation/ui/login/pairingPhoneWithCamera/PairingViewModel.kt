package com.lawmobile.presentation.ui.login.pairingPhoneWithCamera

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.pairingPhoneWithCamera.PairingPhoneWithCameraUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.WifiHelper
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PairingViewModel @Inject constructor(
    private val pairingPhoneWithCameraUseCase: PairingPhoneWithCameraUseCase,
    private val wifiHelper: WifiHelper
) : BaseViewModel() {

    var cameraPairingProgress: ((Result<Int>) -> Unit)? = null

    private val validateConnectionMediatorLiveData: MediatorLiveData<Result<Unit>> =
        MediatorLiveData()
    val validateConnectionLiveData: LiveData<Result<Unit>> get() = validateConnectionMediatorLiveData

    fun getProgressConnectionWithTheCamera() {
        val gateway = wifiHelper.getGatewayAddress()
        val ipAddress = wifiHelper.getIpAddress()
        if (ipAddress.isEmpty() || gateway.isEmpty()) {
            cameraPairingProgress?.invoke(
                Result.Error(Exception(EXCEPTION_GET_PARAMS_TO_CONNECT))
            )
        }

        viewModelScope.launch {
            with(pairingPhoneWithCameraUseCase) {
                cameraPairingProgress?.let { loadPairingCamera(gateway, ipAddress, it) }
            }
        }
    }

    fun isPossibleConnection() {
        val gateway = wifiHelper.getGatewayAddress()
        if (gateway.isEmpty()) {
            validateConnectionMediatorLiveData.postValue(
                Result.Error(Exception(EXCEPTION_GET_PARAMS_TO_CONNECT))
            )
        }
        viewModelScope.launch {
            validateConnectionMediatorLiveData.postValue(
                pairingPhoneWithCameraUseCase.isPossibleTheConnection(
                    gateway
                )
            )
        }
    }

    fun resetProgress() {
        cameraPairingProgress?.invoke(Result.Success(0))
    }

    fun getNetworkName() = wifiHelper.getSSIDWiFi()

    fun isWifiEnable(): Boolean = wifiHelper.isWifiEnable()

    fun cleanCacheFiles() {
        pairingPhoneWithCameraUseCase.cleanCacheFiles()
    }

    companion object {
        const val EXCEPTION_GET_PARAMS_TO_CONNECT =
            "Exception in get params to configure connection"
    }
}
