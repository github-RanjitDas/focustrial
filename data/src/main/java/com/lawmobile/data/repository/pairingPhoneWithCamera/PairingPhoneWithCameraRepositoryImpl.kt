package com.lawmobile.data.repository.pairingPhoneWithCamera

import androidx.lifecycle.LiveData
import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSource
import com.lawmobile.domain.repository.pairingPhoneWithCamera.PairingPhoneWithCameraRepository
import com.safefleet.mobile.commons.helpers.Result

class PairingPhoneWithCameraRepositoryImpl(private val pairingPhoneWithCameraRemoteDataSource: PairingPhoneWithCameraRemoteDataSource) :
    PairingPhoneWithCameraRepository {
    override var progressPairingCamera: LiveData<Result<Int>> =
        pairingPhoneWithCameraRemoteDataSource.progressPairingCamera

    override suspend fun loadPairingCamera(hostnameToConnect: String, ipAddressClient: String) {
        pairingPhoneWithCameraRemoteDataSource.loadPairingCamera(hostnameToConnect, ipAddressClient)
    }

    override fun getSSIDSavedIfExist(): Result<String> {
        return pairingPhoneWithCameraRemoteDataSource.getSSIDSavedIfExist()
    }

    override fun saveSerialNumberOfCamera(serialNumber: String) {
        pairingPhoneWithCameraRemoteDataSource.saveSerialNumberOfCamera(serialNumber)
    }
}