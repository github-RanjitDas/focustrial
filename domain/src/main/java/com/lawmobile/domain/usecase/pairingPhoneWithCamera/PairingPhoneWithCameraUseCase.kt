package com.lawmobile.domain.usecase.pairingPhoneWithCamera

import com.lawmobile.domain.usecase.BaseUseCase
import com.safefleet.mobile.commons.helpers.Result

interface PairingPhoneWithCameraUseCase : BaseUseCase {
    suspend fun loadPairingCamera(
        hostnameToConnect: String,
        ipAddressClient: String,
        progressPairingCamera: ((Result<Int>) -> Unit)
    )

    suspend fun isPossibleTheConnection(hostnameToConnect: String): Result<Unit>
}