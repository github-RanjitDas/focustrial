package com.lawmobile.presentation.ui.login.x1

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.usecase.typeOfCamera.TypeOfCameraUseCase
import com.lawmobile.domain.usecase.validatePasswordOfficer.ValidatePasswordOfficerUseCase
import com.lawmobile.presentation.ui.login.LoginBaseViewModel
import com.lawmobile.presentation.ui.login.model.LoginState
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginX1ViewModel @Inject constructor(
    private val typeOfCameraUseCase: TypeOfCameraUseCase,
    getUserFromCamera: ValidatePasswordOfficerUseCase,
    ioDispatcher: CoroutineDispatcher
) : LoginBaseViewModel(getUserFromCamera, ioDispatcher) {
    val loginState: LiveData<LoginState> get() = _loginState
    private val _loginState by lazy {
        MediatorLiveData<LoginState>().apply { value = LoginState.X1.SplashAnimation }
    }

    private val officerPasswordFromCamera by lazy { MutableLiveData<String>() }

    fun setLoginState(state: LoginState) {
        _loginState.postValue(state)
    }

    fun setOfficerPasswordFromCamera(password: String) {
        officerPasswordFromCamera.value = password
    }

    fun getOfficerPasswordFromCamera(): String = officerPasswordFromCamera.value ?: ""

    fun setCameraType() {
        viewModelScope.launch {
            typeOfCameraUseCase.getTypeOfCamera().doIfSuccess { CameraInfo.setCamera(it) }
        }
    }
}
