package com.lawmobile.presentation.ui.sso

import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.network_manager.ListenableNetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SSOViewModel @Inject constructor(
    private val networkManager: ListenableNetworkManager
) : BaseViewModel() {
    fun getNetworkManager() = networkManager
}
