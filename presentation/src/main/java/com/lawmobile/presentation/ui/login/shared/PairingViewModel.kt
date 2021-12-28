package com.lawmobile.presentation.ui.login.shared

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

    val connectionProgress: LiveData<Result<Int>> get() = _connectionProgress
    private val _connectionProgress =
        MediatorLiveData<Result<Int>>().apply { value = Result.Success(0) }

    val isConnectionPossible: LiveData<Result<Unit>> get() = _isConnectionPossible
    private val _isConnectionPossible: MediatorLiveData<Result<Unit>> = MediatorLiveData()

    fun connectWithCamera() {
        if (connectionProgress.value == Result.Success(0)) {
            val gateway = wifiHelper.getGatewayAddress()
            val ipAddress = wifiHelper.getIpAddress()
            if (ipAddress.isEmpty() || gateway.isEmpty()) {
                _connectionProgress.value = Result.Error(Exception(EXCEPTION_GET_PARAMS_TO_CONNECT))
            }

            viewModelScope.launch {
                with(pairingPhoneWithCameraUseCase) {
                    loadPairingCamera(gateway, ipAddress) { _connectionProgress.postValue(it) }
                }
            }
        }
    }

    fun isConnectionPossible() {
        val gateway = wifiHelper.getGatewayAddress()
        if (gateway.isEmpty()) {
            _isConnectionPossible.postValue(
                Result.Error(Exception(EXCEPTION_GET_PARAMS_TO_CONNECT))
            )
        }
        viewModelScope.launch {
            _isConnectionPossible.postValue(
                pairingPhoneWithCameraUseCase.isPossibleTheConnection(gateway)
            )
        }
    }

    fun getNetworkName() = wifiHelper.getSSIDWiFi()

    fun isWifiEnable(): Boolean = wifiHelper.isWifiEnable()

    fun cleanCacheFiles() {
        pairingPhoneWithCameraUseCase.cleanCacheFiles()
    }

    fun resetProgress() {
        _connectionProgress.value = Result.Success(0)
    }

    companion object {
        const val EXCEPTION_GET_PARAMS_TO_CONNECT =
            "Exception in get params to configure connection"
    }
}
