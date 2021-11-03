package com.lawmobile.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainUser
import com.lawmobile.domain.usecase.validateOfficerPassword.ValidateOfficerPasswordUseCase
import com.lawmobile.presentation.BuildConfig
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.AuthStateManagerFactory
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
    private val validateOfficerPasswordUseCase: ValidateOfficerPasswordUseCase,
    private val authStateManagerFactory: AuthStateManagerFactory,
    private val IODispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val authStateManager by lazy { authStateManagerFactory.create() }

    val userInformationResult: LiveData<Result<DomainUser>> get() = _userInformationResult
    private val _userInformationResult by lazy { MediatorLiveData<Result<DomainUser>>() }

    val authRequestResult: LiveData<Result<AuthorizationRequest>> get() = _authRequestResult
    private val _authRequestResult by lazy { MediatorLiveData<Result<AuthorizationRequest>>() }

    fun getUserInformation() {
        viewModelScope.launch {
            _userInformationResult.postValue(validateOfficerPasswordUseCase.getUserInformation())
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

    fun getAuthRequest() {
        viewModelScope.launch(IODispatcher) {
            _authRequestResult.postValue(authStateManager.createRequestToAuth())
        }
    }
}
