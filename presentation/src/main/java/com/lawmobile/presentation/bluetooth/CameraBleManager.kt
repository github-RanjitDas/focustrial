package com.lawmobile.presentation.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.bluetooth.le.ScanSettings.CALLBACK_TYPE_FIRST_MATCH
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import com.lawmobile.domain.entities.CameraInfo
import java.util.UUID
import kotlin.collections.ArrayList

class CameraBleManager {
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothLeScanner: BluetoothLeScanner
    private var scanning = false
    private var isCameraDetected = false
    private val handler = Handler(Looper.myLooper()!!)
    private lateinit var onBleStatusUpdates: OnBleStatusUpdates

    fun initManager(context: Context) {
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

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
            Log.d(TAG, "Scanning Device:" + result.device.name + "," + result.device.address)

            if (result.device.name.equals("X_" + CameraInfo.officerId, true)) {
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
        xBleDevice.connectGatt(context, true, bluetoothGattCallback, 2)
    }

    private val bluetoothGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                println("onConnectionStateChange : -------------")
                // successfully connected to the GATT Server
                gatt.requestMtu(512)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                gatt.disconnect()
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
                val characteristic = service?.getCharacteristic(BLE_CHAR_UUID)
                gatt?.readCharacteristic(characteristic)
            } else {
                Log.e(TAG, "Bluetooth Gatt Failed.")
                onBleStatusUpdates.onFailedFetchConfig()
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?,
            characteristic: BluetoothGattCharacteristic?,
            status: Int
        ) {
            Log.d(TAG, "onCharacteristicRead:" + characteristic?.getStringValue(0))
            onBleStatusUpdates.onDataReceived(characteristic?.getStringValue(0))
            gatt?.close()
        }
    }

    fun setListener(onBleStatusUpdates: OnBleStatusUpdates) {
        this.onBleStatusUpdates = onBleStatusUpdates
    }

    companion object {
        internal const val TAG = "CameraBleManager"
        private val BLE_SERVICE_UUID = convertFromInteger(0xFFA1)
        private val BLE_CHAR_UUID = convertFromInteger(0xFFF3)

        // Stops scanning after 20 seconds.
        private const val SCAN_PERIOD: Long = 30000
        private fun convertFromInteger(i: Int): UUID {
            val MSB = 0x0000000000001000L
            val LSB = -0x7fffff7fa064cb05L
            val value = (i and -0x1).toLong()
            return UUID(MSB or (value shl 32), LSB)
        }
    }
}
