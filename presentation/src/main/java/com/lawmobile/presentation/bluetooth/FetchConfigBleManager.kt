package com.lawmobile.presentation.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.CALLBACK_TYPE_FIRST_MATCH
import android.bluetooth.le.ScanSettings.MATCH_MODE_AGGRESSIVE
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.lawmobile.domain.entities.CameraInfo
import kotlin.collections.ArrayList

@SuppressLint("MissingPermission")
class FetchConfigBleManager : BaseBleManager() {
    var context: Context? = null

    fun doStartScanning(param: OnBleStatusUpdates) {
        setListener(param)
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed(
                {
                    scanning = false
                    bluetoothLeScanner.stopScan(leScanCallback)
                    if (!isCameraDetected) {
                        val bluetoothNameToFind = "X_" + CameraInfo.officerId
                        Log.e(TAG, "$bluetoothNameToFind Camera Not Found.")
                        onBleStatusUpdates.onFailedFetchConfig()
                    }
                },
                SCAN_PERIOD
            )
            scanning = true
            val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(CALLBACK_TYPE_FIRST_MATCH)
                .setMatchMode(MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .build()
            val filters: MutableList<ScanFilter?> = ArrayList()
            val filter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(BLE_SERVICE_UUID))
                .setDeviceName("X_" + CameraInfo.officerId)
                .build()
            filters.add(filter)
            isCameraDetected = false
            Log.d(TAG, "Starting BLE Scan...")
            val bluetoothNameToFind = "X_" + CameraInfo.officerId
            Log.d(TAG, "Searching Camera with name $bluetoothNameToFind in the Nearby devices.")
            bluetoothLeScanner.startScan(filters, settings, leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d(
                TAG,
                "Scanning Nearby Devices: Name:" + result.device.name + ", ScanRecordName:" +
                    result.scanRecord?.deviceName + ", Address:" + result.device.address
            )
            val bluetoothNameToFind = "X_" + CameraInfo.officerId
            if (result.device.name.equals(bluetoothNameToFind, true) ||
                result.scanRecord?.deviceName.equals(bluetoothNameToFind, true)
            ) {
                isCameraDetected = true
                scanning = false
                Log.d(
                    TAG,
                    "Camera FOUND Successfully:" + result.device.name + "," + result.scanRecord?.deviceName
                )
                bluetoothLeScanner.stopScan(this)
                onBleStatusUpdates.onScanResult(callbackType, result)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Log.e(TAG, "Scan Failed:$errorCode")
            onBleStatusUpdates.onFailedFetchConfig()
        }
    }

    fun doConnectGatt(context: Context, xBleDevice: BluetoothDevice) {
        xBleDevice.connectGatt(context, false, bluetoothGattCallback, 2)
    }

    private val bluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG, "Camera Connected Successfully")
                    // successfully connected to the GATT Server
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.e(TAG, "Camera get Disconnected")
                    // disconnected from the GATT Server
                    gatt.disconnect()
                }
            } else {
                gatt.close()
                onBleStatusUpdates.onFailedFetchConfig()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt?.getService(BLE_SERVICE_UUID)
                val characteristic = service?.getCharacteristic(BLE_CHAR_CONFIGS_UUID)
                gatt?.readCharacteristic(characteristic)
            } else {
                Log.e(TAG, "Bluetooth Gatt Failed.")
                gatt?.close()
                onBleStatusUpdates.onFailedFetchConfig()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Log.d(TAG, "Successfully Received Configs from Camera: " + characteristic?.getStringValue(0))
            gatt?.close()
            onBleStatusUpdates.onDataReceived(characteristic?.getStringValue(0))
        }
    }
}
