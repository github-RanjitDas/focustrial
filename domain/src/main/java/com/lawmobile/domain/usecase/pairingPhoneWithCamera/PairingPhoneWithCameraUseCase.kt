package com.lawmobile.domain.usecase.pairingPhoneWithCamera

import androidx.lifecycle.LiveData
import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.commons.helpers.Result

interface PairingPhoneWithCameraUseCase : BaseUseCase {
    val progressPairingCamera: LiveData<Result<Int>>
    suspend fun loadPairingCamera(hostnameToConnect: String, ipAddressClient: String)
}