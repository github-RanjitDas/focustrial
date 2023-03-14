package com.lawmobile.presentation.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult

interface OnBleStatusUpdates {
    fun onScanResult(callbackType: Int, result: ScanResult)
    fun onPairedDeviceFound(bluetoothDevice: BluetoothDevice) {}
    fun onDataReceived(data: String?)
    fun onFailedFetchConfig()
}
