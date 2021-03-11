package com.lawmobile.data.datasource.remote.pairingPhoneWithCamera

import com.lawmobile.data.utils.CameraServiceFactory
import com.lawmobile.domain.entities.CacheManager
import com.safefleet.mobile.kotlin_commons.helpers.Result

open class PairingPhoneWithCameraRemoteDataSourceImpl(
    cameraServiceFactory: CameraServiceFactory
) : PairingPhoneWithCameraRemoteDataSource {

    private var cameraService = cameraServiceFactory.create()

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

    override fun cleanCacheFiles() {
        CacheManager.cleanFileLists()
        cameraService.cleanCacheFiles()
    }
}
