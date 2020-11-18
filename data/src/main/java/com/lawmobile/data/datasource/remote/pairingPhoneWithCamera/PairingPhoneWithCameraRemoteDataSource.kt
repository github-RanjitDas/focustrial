package com.lawmobile.data.datasource.remote.pairingPhoneWithCamera

import androidx.lifecycle.LiveData
import com.safefleet.mobile.commons.helpers.Result

interface PairingPhoneWithCameraRemoteDataSource {
    val progressPairingCamera: LiveData<Result<Int>>
    suspend fun loadPairingCamera(hostnameToConnect: String, ipAddressClient: String)
}