package com.lawmobile.presentation.ui.login.validateOfficerId

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.validateOfficerId.ValidateOfficerIdUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.android_commons.helpers.network_manager.ListenableNetworkManager
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ValidateOfficerIdViewModel @Inject constructor(
    private val validateOfficerIdUseCase: ValidateOfficerIdUseCase,
    private val simpleNetworkManager: ListenableNetworkManager,
    private val backgroundDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val validateOfficerIdResult: LiveData<Result<Boolean>> get() = _validateOfficerIdResult
    private val _validateOfficerIdResult = MutableLiveData<Result<Boolean>>()

    fun validateOfficerId(officerId: String) {
        viewModelScope.launch(backgroundDispatcher) {
            _validateOfficerIdResult.postValue(validateOfficerIdUseCase.validateOfficerId(officerId))
        }
    }

    fun verifyInternetConnection(callback: (Boolean) -> Unit) {
        viewModelScope.launch(backgroundDispatcher) {
            simpleNetworkManager.verifyInternetConnection(callback)
        }
    }
}
