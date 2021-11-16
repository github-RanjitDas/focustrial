package com.lawmobile.presentation.ui.login.validateOfficerId

import androidx.lifecycle.viewModelScope
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.android_commons.helpers.network_manager.ListenableNetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ValidateOfficerIdViewModel @Inject constructor(
    private val simpleNetworkManager: ListenableNetworkManager,
    private val backgroundDispatcher: CoroutineDispatcher
) : BaseViewModel() {
    fun verifyInternetConnection(callback: (Boolean) -> Unit) {
        viewModelScope.launch(backgroundDispatcher) {
            simpleNetworkManager.verifyInternetConnection(callback)
        }
    }
}
