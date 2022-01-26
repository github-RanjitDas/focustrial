package com.lawmobile.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.User
import com.lawmobile.domain.usecase.getUserFromCamera.GetUserFromCamera
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

open class LoginBaseViewModel(
    private val getUserFromCameraUseCase: GetUserFromCamera,
    private val wifiHelper: WifiHelper,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    var isInstructionsOpen = false

    val userFromCameraResult: LiveData<Result<User>> get() = _userFromCameraResult
    private val _userFromCameraResult by lazy { MediatorLiveData<Result<User>>() }

    fun getUserFromCamera() {
        viewModelScope.launch(ioDispatcher) {
            _userFromCameraResult.postValue(getUserFromCameraUseCase())
        }
    }

    fun suggestWiFiNetwork(
        networkName: String,
        networkPassword: String,
        connectionCallback: (connected: Boolean) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            wifiHelper.suggestWiFiNetwork(networkName, networkPassword, connectionCallback)
        }
    }
}
