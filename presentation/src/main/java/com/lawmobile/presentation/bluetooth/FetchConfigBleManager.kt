package com.lawmobile.presentation.bluetooth

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
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.lawmobile.domain.entities.CameraInfo
import kotlin.collections.ArrayList

class FetchConfigBleManager : BaseBleManager() {

    fun doStartScanning(param: OnBleStatusUpdates) {
        setListener(param)
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed(
                {
                    scanning = false
                    bluetoothLeScanner.stopScan(leScanCallback)
                    if (!isCameraDetected) {
                        onBleStatusUpdates.onFailedFetchConfig()
                    }
                },
                SCAN_PERIOD
            )
            scanning = true
            val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(CALLBACK_TYPE_FIRST_MATCH)
                // .setMatchMode(MATCH_MODE_AGGRESSIVE)
                // .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
                .build()
            val filters: MutableList<ScanFilter?> = ArrayList()
            val filter = ScanFilter.Builder()
                .setServiceUuid(ParcelUuid(BLE_SERVICE_UUID))
                .build()
            filters.add(filter)
            isCameraDetected = false
            bluetoothLeScanner.startScan(filters, settings, leScanCallback)
        } else {
            scanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d(
                TAG,
                "Scanning Device:" + result.device.name + "," + result.device.address + ", " + result.scanRecord?.deviceName
            )
            val bluetoothNameToFind = "X_" + CameraInfo.officerId
            if (result.device.name.equals(bluetoothNameToFind, true) ||
                result.scanRecord?.deviceName.equals(bluetoothNameToFind, true)
            ) {
                isCameraDetected = true
                Log.d(TAG, "Camera FOUND:" + result.device.name + "," + result.device.address)
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
                    Log.d(TAG, "Device get Connected")
                    // successfully connected to the GATT Server
                    gatt.requestMtu(512)
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d(TAG, "Device get Disconnect")
                    // disconnected from the GATT Server
                    gatt.disconnect()
                }
            } else {
                gatt.close()
                onBleStatusUpdates.onFailedFetchConfig()
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
            Log.d(TAG, "onMtuChanged:$mtu")
            // Call to discover services...
            gatt?.discoverServices()
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
            Log.d(TAG, "Received Configs from Bluetooth: " + characteristic?.getStringValue(0))
            gatt?.close()
            onBleStatusUpdates.onDataReceived(characteristic?.getStringValue(0))
        }
    }
}
