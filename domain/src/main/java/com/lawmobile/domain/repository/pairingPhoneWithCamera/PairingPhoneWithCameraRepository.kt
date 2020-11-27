package com.lawmobile.domain.repository.pairingPhoneWithCamera

import com.lawmobile.domain.repository.BaseRepository
import com.safefleet.mobile.commons.helpers.Result

interface PairingPhoneWithCameraRepository : BaseRepository {
    suspend fun loadPairingCamera(
        hostnameToConnect: String,
        ipAddressClient: String,
        progressPairingCamera: ((Result<Int>) -> Unit)
    )

    suspend fun isPossibleTheConnection(hostnameToConnect: String): Result<Unit>
}