package com.lawmobile.data.datasource.remote.pairingPhoneWithCamera

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.safefleet.mobile.avml.cameras.x1.CameraDataSource
import com.safefleet.mobile.commons.helpers.Result
import kotlin.Exception

open class PairingPhoneWithCameraRemoteDataSourceImpl(
    private val preferences: SharedPreferences,
    private val cameraDataSource: CameraDataSource
) :
    PairingPhoneWithCameraRemoteDataSource {
    override var progressPairingCamera: LiveData<Result<Int>> =
        cameraDataSource.progressPairingCamera

    override suspend fun loadPairingCamera(hostnameToConnect: String, ipAddressClient: String) {
        cameraDataSource.loadPairingCamera(hostnameToConnect, ipAddressClient)
    }

    override fun getSSIDSavedIfExist(): Result<String> {
        val lasSerialNumber =
            preferences.getString(LAST_SERIAL_NUMBER_TO_CONNECTION, DEFAULT_SERIAL_NUMBER)
        if (lasSerialNumber == null || lasSerialNumber == DEFAULT_SERIAL_NUMBER) {
            return Result.Error(Exception(ERROR_DOES_NOT_SSID_SAVED))
        }

        return Result.Success(lasSerialNumber)
    }

    override fun saveSerialNumberOfCamera(serialNumber: String) {
        val editor = preferences.edit()
        editor.putString(LAST_SERIAL_NUMBER_TO_CONNECTION, serialNumber)
        editor.apply()
    }

    companion object {
        const val LAST_SERIAL_NUMBER_TO_CONNECTION = "LAST_SERIAL_NUMBER_TO_CONNECTION"
        const val DEFAULT_SERIAL_NUMBER = "57"
        const val ERROR_DOES_NOT_SSID_SAVED = "Does not exist any serial number saved yet"
    }
}