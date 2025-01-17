package com.lawmobile.data.datasource.remote.pairingPhoneWithCamera

import com.safefleet.mobile.kotlin_commons.helpers.Result

interface PairingPhoneWithCameraRemoteDataSource {
    suspend fun loadPairingCamera(
        hostnameToConnect: String,
        ipAddressClient: String,
        progressPairingCamera: ((Result<Int>) -> Unit)
    )

    suspend fun isPossibleTheConnection(hostnameToConnect: String): Result<Unit>

    fun cleanCacheFiles()
}
