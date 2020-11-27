package com.lawmobile.data.repository.pairingPhoneWithCamera

import com.lawmobile.data.datasource.remote.pairingPhoneWithCamera.PairingPhoneWithCameraRemoteDataSource
import com.lawmobile.domain.repository.pairingPhoneWithCamera.PairingPhoneWithCameraRepository
import com.safefleet.mobile.commons.helpers.Result

class PairingPhoneWithCameraRepositoryImpl(private val pairingPhoneWithCameraRemoteDataSource: PairingPhoneWithCameraRemoteDataSource) :
    PairingPhoneWithCameraRepository {

    override suspend fun loadPairingCamera(
        hostnameToConnect: String,
        ipAddressClient: String,
        progressPairingCamera: ((Result<Int>) -> Unit)
    ) {
        pairingPhoneWithCameraRemoteDataSource.loadPairingCamera(
            hostnameToConnect,
            ipAddressClient,
            progressPairingCamera
        )
    }

    override suspend fun isPossibleTheConnection(hostnameToConnect: String): Result<Unit> {
        return pairingPhoneWithCameraRemoteDataSource.isPossibleTheConnection(hostnameToConnect)
    }
}