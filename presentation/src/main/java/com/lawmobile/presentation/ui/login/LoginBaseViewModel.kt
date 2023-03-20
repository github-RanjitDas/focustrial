package com.lawmobile.presentation.ui.login

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.User
import com.lawmobile.domain.usecase.getUserFromCamera.GetUserFromCamera
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.ui.login.state.LoginState
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class LoginBaseViewModel(
    private val getUserFromCameraUseCase: GetUserFromCamera,
    private val wifiHelper: WifiHelper,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel() {

    val loginState: StateFlow<LoginState> get() = mutableLoginState
    protected abstract val mutableLoginState: MutableStateFlow<LoginState>
    private var retryCounter = 1
    fun setLoginState(state: LoginState) {
        mutableLoginState.value = state
    }

    fun getLoginState(): LoginState = mutableLoginState.value

    var isInstructionsOpen = false

    val userFromCameraResult: LiveData<Result<User>> get() = _userFromCameraResult
    private val _userFromCameraResult by lazy { MediatorLiveData<Result<User>>() }

    fun getUserFromCamera() {
        viewModelScope.launch(ioDispatcher) {
            val userResult = getUserFromCameraUseCase()
            userResult.doIfError {
                if (MAX_RETRY_ATTEMPT >= retryCounter) {
                    println("Failed So Retry getUserFromCamera RetryCount:$retryCounter")
                    retryCounter++
                    getUserFromCamera()
                } else {
                    _userFromCameraResult.postValue(userResult)
                }
            }
            userResult.doIfSuccess {
                _userFromCameraResult.postValue(userResult)
            }
        }
    }

    fun suggestWiFiNetwork(
        handler: Handler,
        networkName: String,
        networkPassword: String,
        connectionCallback: (connected: Boolean) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            wifiHelper.suggestWiFiNetwork(handler, networkName, networkPassword, connectionCallback)
        }
    }

    companion object {
        private const val MAX_RETRY_ATTEMPT = 2
    }
}
