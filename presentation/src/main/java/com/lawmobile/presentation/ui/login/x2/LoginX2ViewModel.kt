package com.lawmobile.presentation.ui.login.x2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.usecase.LoginUseCases
import com.lawmobile.domain.utils.PreferencesManager
import com.lawmobile.presentation.BuildConfig
import com.lawmobile.presentation.authentication.AuthStateManagerFactory
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.ui.login.LoginBaseViewModel
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
class LoginX2ViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases,
    private val authStateManagerFactory: AuthStateManagerFactory,
    private val preferencesManager: PreferencesManager,
    private val ioDispatcher: CoroutineDispatcher,
    wifiHelper: WifiHelper
) : LoginBaseViewModel(loginUseCases.getUserFromCamera, wifiHelper, ioDispatcher) {

    private lateinit var authStateManager: AuthStateManager

    val authEndpointsResult: LiveData<Result<AuthorizationEndpoints>> get() = _authEndpointsResult
    private val _authEndpointsResult by lazy { MediatorLiveData<Result<AuthorizationEndpoints>>() }

    val authRequestResult: LiveData<Result<AuthorizationRequest>> get() = _authRequestResult
    private val _authRequestResult by lazy { MediatorLiveData<Result<AuthorizationRequest>>() }

    val devicePasswordResult: LiveData<Result<String>> get() = _devicePasswordResult
    private val _devicePasswordResult by lazy { MediatorLiveData<Result<String>>() }

    fun getAuthorizationEndpoints() {
        viewModelScope.launch(ioDispatcher) {
            _authEndpointsResult.postValue(loginUseCases.getAuthorizationEndpoints())
        }
    }

    fun getDevicePassword(uuid: String) {
        viewModelScope.launch(ioDispatcher) {
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

        viewModelScope.launch(ioDispatcher) {
            _authRequestResult.postValue(authStateManager.createRequestToAuth())
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch(ioDispatcher) {
            preferencesManager.saveToken(token)
        }
    }
}
