package com.lawmobile.presentation.ui.login.validateOfficerId

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.SimpleNetworkManager
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ValidateOfficerIdViewModel @Inject constructor(
    private val simpleNetworkManager: SimpleNetworkManager,
    private val backgroundDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    val validateOfficerIdResult: LiveData<Result<Unit>> get() = _validateOfficerIdResult
    private val _validateOfficerIdResult = MutableLiveData<Result<Unit>>()

    // pending to implement real behavior
    fun validateOfficerId(officerId: String) {
        viewModelScope.launch(backgroundDispatcher) {
            _validateOfficerIdResult.postValue(Result.Error(Exception(officerId)))
        }
    }

    fun verifyInternetConnection(callback: (Boolean) -> Unit) {
        viewModelScope.launch(backgroundDispatcher) {
            simpleNetworkManager.verifyInternetConnection(callback)
        }
    }
}
