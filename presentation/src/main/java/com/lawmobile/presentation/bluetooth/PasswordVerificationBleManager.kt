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
import android.content.Context
import android.os.ParcelUuid
import android.util.Log
import com.lawmobile.domain.entities.CameraInfo

@SuppressLint("MissingPermission")
class PasswordVerificationBleManager : BaseBleManager() {
    var inputPassword: String = ""

    fun doStartScanning(param: OnBleStatusUpdates) {
        Log.d(TAG, "Start password verification from BLE...$scanning")
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
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .build()
            val filters: MutableList<ScanFilter?> = ArrayList()
            val filter = ScanFilter.Builder().setServiceUuid(ParcelUuid(BLE_SERVICE_UUID)).build()
            filters.add(filter)
            isCameraDetected = false
            Log.d(TAG, "Starting BLE Scan for Validation...")
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
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            Log.d(
                TAG,
                "Scanning Nearby Devices: Name:" + result.device.name + ", ScanRecordName:" +
                    result.scanRecord?.deviceName + ", Address:" + result.device.address
            )
            val bluetoothNameToFind = "X_" + CameraInfo.officerId
            if (result.device.name.equals(
                    bluetoothNameToFind,
                    true
                ) || result.scanRecord?.deviceName.equals(bluetoothNameToFind, true)
            ) {
                isCameraDetected = true
                scanning = false
                Log.d(
                    TAG,
                    "Camera FOUND Successfully:" + result.device.name + "," + result.scanRecord?.deviceName
                )
                handler.removeCallbacksAndMessages(null)
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
        xBleDevice.connectGatt(context, false, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    private val bluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            Log.d(TAG, "onConnectionStateChange:$status,$newState")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // successfully connected to the GATT Server
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
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
                val characteristic = service?.getCharacteristic(BLE_CHAR_PASSWORD_VERIFICATION_UUID)
                characteristic?.setValue(inputPassword)
                Log.e(TAG, "Bluetooth Write Value:$inputPassword")
                gatt?.writeCharacteristic(characteristic)
            } else {
                gatt?.close()
                Log.e(TAG, "Bluetooth Gatt Failed.")
                onBleStatusUpdates.onFailedFetchConfig()
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            Log.d(TAG, "onCharacteristicWrite Done:$status")
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt?.readCharacteristic(characteristic)
            } else {
                onBleStatusUpdates.onFailedFetchConfig()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Log.d(TAG, "onCharacteristicRead:" + characteristic?.getStringValue(0))
            gatt?.close()
            onBleStatusUpdates.onDataReceived(characteristic?.getStringValue(0))
        }
    }
}
