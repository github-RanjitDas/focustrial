package com.lawmobile.data.datasource.remote.pairingPhoneWithCamera

import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.external_hardware.cameras.CameraService

open class PairingPhoneWithCameraRemoteDataSourceImpl(
    private val cameraService: CameraService
) : PairingPhoneWithCameraRemoteDataSource {

    override suspend fun loadPairingCamera(
        hostnameToConnect: String,
        ipAddressClient: String,
        progressPairingCamera: ((Result<Int>) -> Unit)
    ) {
        cameraService.progressPairingCamera = progressPairingCamera
        cameraService.loadPairingCamera(hostnameToConnect, ipAddressClient)
    }

    override suspend fun isPossibleTheConnection(hostnameToConnect: String): Result<Unit> {
        return cameraService.isPossibleTheConnection(hostnameToConnect)
    }
}