package com.lawmobile.data.datasource.remote.pairingPhoneWithCamera

import androidx.lifecycle.LiveData
import com.safefleet.mobile.avml.cameras.external.CameraConnectService
import com.safefleet.mobile.commons.helpers.Result

open class PairingPhoneWithCameraRemoteDataSourceImpl(
    private val cameraConnectService: CameraConnectService
) :
    PairingPhoneWithCameraRemoteDataSource {
    override var progressPairingCamera: LiveData<Result<Int>> =
        cameraConnectService.progressPairingCamera

    override suspend fun loadPairingCamera(hostnameToConnect: String, ipAddressClient: String) {
        cameraConnectService.loadPairingCamera(hostnameToConnect, ipAddressClient)
    }

    override suspend fun isPossibleTheConnection(hostnameToConnect: String): Result<Unit> {
        return cameraConnectService.isPossibleTheConnection(hostnameToConnect)
    }
}