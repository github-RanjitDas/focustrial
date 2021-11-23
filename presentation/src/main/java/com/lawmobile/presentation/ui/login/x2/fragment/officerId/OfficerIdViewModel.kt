package com.lawmobile.presentation.ui.login.x2.fragment.officerId

import android.bluetooth.BluetoothAdapter
import androidx.lifecycle.viewModelScope
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.android_commons.helpers.network_manager.ListenableNetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OfficerIdViewModel @Inject constructor(
    private val simpleNetworkManager: ListenableNetworkManager,
    private val backgroundDispatcher: CoroutineDispatcher,
    private val bluetoothAdapter: BluetoothAdapter
) : BaseViewModel() {
    fun verifyInternetConnection(callback: (Boolean) -> Unit) {
        viewModelScope.launch(backgroundDispatcher) {
            simpleNetworkManager.verifyInternetConnection(callback)
        }
    }

    fun verifyBluetoothConnection(callback: (Boolean) -> Unit) {
        viewModelScope.launch(backgroundDispatcher) {
            callback(bluetoothAdapter.isEnabled)
        }
    }
}
