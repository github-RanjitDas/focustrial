package com.lawmobile.domain.usecase.pairingPhoneWithCamera

import com.lawmobile.domain.repository.pairingPhoneWithCamera.PairingPhoneWithCameraRepository
import com.safefleet.mobile.commons.helpers.Result

class PairingPhoneWithCameraUseCaseImpl(private val pairingPhoneWithCameraRepository: PairingPhoneWithCameraRepository) :
    PairingPhoneWithCameraUseCase {

    override suspend fun loadPairingCamera(
        hostnameToConnect: String,
        ipAddressClient: String,
        progressPairingCamera: ((Result<Int>) -> Unit)
    ) {
        pairingPhoneWithCameraRepository.loadPairingCamera(
            hostnameToConnect,
            ipAddressClient,
            progressPairingCamera
        )
    }

    override suspend fun isPossibleTheConnection(hostnameToConnect: String): Result<Unit> {
        return pairingPhoneWithCameraRepository.isPossibleTheConnection(hostnameToConnect)
    }

}