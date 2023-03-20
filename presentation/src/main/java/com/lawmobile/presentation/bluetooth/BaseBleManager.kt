package com.lawmobile.presentation.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.UUID

open class BaseBleManager {
    protected lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothManager: BluetoothManager
    protected lateinit var bluetoothLeScanner: BluetoothLeScanner
    protected var scanning = false
    protected var isCameraDetected = false
    protected val handler = Handler(Looper.myLooper()!!)
    protected lateinit var onBleStatusUpdates: OnBleStatusUpdates

    fun initManager(context: Context) {
        bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

    fun setListener(onBleStatusUpdates: OnBleStatusUpdates) {
        this.onBleStatusUpdates = onBleStatusUpdates
    }

    companion object {

        @JvmStatic
        protected val TAG = "CameraBleManager"

        @JvmStatic
        protected val BLE_SERVICE_UUID = convertFromInteger(0xFFA1)

        @JvmStatic
        protected val BLE_CHAR_CONFIGS_UUID = convertFromInteger(0xFFF3)

        @JvmStatic
        protected val BLE_CHAR_PASSWORD_VERIFICATION_UUID = convertFromInteger(0xFFF4)

        // Stops scanning after 30 seconds.
        @JvmStatic
        protected val SCAN_PERIOD: Long = 20000
        private fun convertFromInteger(i: Int): UUID {
            val MSB = 0x0000000000001000L
            val LSB = -0x7fffff7fa064cb05L
            val value = (i and -0x1).toLong()
            return UUID(MSB or (value shl 32), LSB)
        }
    }
}
