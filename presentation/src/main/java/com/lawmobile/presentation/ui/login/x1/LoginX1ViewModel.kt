package com.lawmobile.presentation.ui.login.x1

import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.usecase.typeOfCamera.TypeOfCameraUseCase
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.ui.login.LoginBaseViewModel
import com.lawmobile.presentation.ui.login.model.LoginState
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginX1ViewModel @Inject constructor(
    private val typeOfCameraUseCase: TypeOfCameraUseCase,
    getUserFromCamera: ValidatePasswordOfficerUseCase,
    ioDispatcher: CoroutineDispatcher
) : LoginBaseViewModel(getUserFromCamera, ioDispatcher) {

    val loginState: StateFlow<LoginState> get() = _loginState
    private val _loginState by lazy { MutableStateFlow<LoginState>(LoginState.X1.SplashAnimation) }

    var officerPasswordFromCamera: String = ""

    fun setLoginState(state: LoginState) {
        _loginState.value = state
    }

    fun getLoginState(): LoginState = _loginState.value

    fun setCameraType() {
        viewModelScope.launch {
            typeOfCameraUseCase.getTypeOfCamera().doIfSuccess { CameraInfo.setCamera(it) }
        }
    }
}
