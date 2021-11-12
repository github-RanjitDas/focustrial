package com.lawmobile.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.entities.User
import com.lawmobile.domain.usecase.LoginUseCases
import com.lawmobile.domain.utils.PreferencesManager
import com.lawmobile.presentation.BuildConfig
import com.lawmobile.presentation.authentication.AuthStateManagerFactory
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.authentication.AuthStateManager
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases,
    private val authStateManagerFactory: AuthStateManagerFactory,
    private val preferencesManager: PreferencesManager,
    private val IODispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private lateinit var authStateManager: AuthStateManager

    val authEndpointsResult: LiveData<Result<AuthorizationEndpoints>> get() = _authEndpointsResult
    private val _authEndpointsResult by lazy { MediatorLiveData<Result<AuthorizationEndpoints>>() }

    val authRequestResult: LiveData<Result<AuthorizationRequest>> get() = _authRequestResult
    private val _authRequestResult by lazy { MediatorLiveData<Result<AuthorizationRequest>>() }

    val devicePasswordResult: LiveData<Result<String>> get() = _devicePasswordResult
    private val _devicePasswordResult by lazy { MediatorLiveData<Result<String>>() }

    val userFromCameraResult: LiveData<Result<User>> get() = _userFromCameraResult
    private val _userFromCameraResult by lazy { MediatorLiveData<Result<User>>() }

    fun getAuthorizationEndpoints() {
        viewModelScope.launch(IODispatcher) {
            _authEndpointsResult.postValue(loginUseCases.getAuthorizationEndpoints())
        }
    }

    fun getDevicePassword(uuid: String) {
        viewModelScope.launch(IODispatcher) {
            _devicePasswordResult.postValue(loginUseCases.getDevicePassword(uuid))
        }
    }

    fun isUserAuthorized() = authStateManager.isCurrentAuthStateAuthorized()

    fun authenticateToGetToken(
        response: AuthorizationResponse,
        callback: (Result<TokenResponse>) -> Unit
    ) {
        authStateManager.exchangeAuthorizationCode(
            response,
            BuildConfig.SSO_CLIENT_SECRET,
            callback
        )
    }

    fun getAuthorizationRequest(authorizationEndpoints: AuthorizationEndpoints) {
        if (!this::authStateManager.isInitialized) {
            authStateManager = authStateManagerFactory.create(authorizationEndpoints)
        }

        viewModelScope.launch(IODispatcher) {
            _authRequestResult.postValue(authStateManager.createRequestToAuth())
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch(IODispatcher) {
            preferencesManager.saveToken(token)
        }
    }

    fun getUserFromCamera() {
        viewModelScope.launch {
            _userFromCameraResult.postValue(loginUseCases.getUserFromCamera())
        }
    }
}
