package com.lawmobile.presentation.ui.login.x2.fragment.devicePassword

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.presentation.bluetooth.OnBleStatusUpdates
import com.lawmobile.presentation.bluetooth.PasswordVerificationBleManager
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicePasswordViewModel @Inject constructor(private val bleManager: PasswordVerificationBleManager) :
    BaseViewModel() {
    var officerId = ""
    var officerPassword = ""
    val updatePasswordVerificationProgress: LiveData<Result<String>> get() = _updatePasswordVerificationProgress
    private val _updatePasswordVerificationProgress by lazy { MediatorLiveData<Result<String>>() }

    internal fun verifyPasswordFromBle(context: Context, inputPassword: String) {
        bleManager.initManager(context)
        bleManager.inputPassword = inputPassword
        scanNConnectFromBluetooth(context)
    }

    private fun scanNConnectFromBluetooth(
        context: Context
    ) {
        bleManager.doStartScanning(object : OnBleStatusUpdates {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                bleManager.doConnectGatt(context, result.device)
            }

            override fun onDataReceived(data: String?) {
                viewModelScope.launch {
                    _updatePasswordVerificationProgress.value = Result.Success(data as String)
                }
            }

            override fun onPairedDeviceFound(bluetoothDevice: BluetoothDevice) {
                bleManager.doConnectGatt(context, bluetoothDevice)
            }

            override fun onFailedFetchConfig() {
                viewModelScope.launch {
                    _updatePasswordVerificationProgress.value =
                        Result.Error(Exception("Error connecting with bluetooth"))
                }
            }
        })
    }
}
