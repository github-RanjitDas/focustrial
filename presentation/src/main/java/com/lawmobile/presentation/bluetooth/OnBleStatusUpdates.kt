package com.lawmobile.presentation.bluetooth

import android.bluetooth.le.ScanResult

interface OnBleStatusUpdates {
    fun onScanResult(callbackType: Int, result: ScanResult)
    fun onDataReceived(data: String?)
    fun onFailedFetchConfig()
}
