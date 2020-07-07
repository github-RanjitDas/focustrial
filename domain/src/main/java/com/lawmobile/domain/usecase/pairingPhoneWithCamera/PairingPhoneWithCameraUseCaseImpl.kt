package com.lawmobile.domain.usecase.pairingPhoneWithCamera

import androidx.lifecycle.LiveData
import com.lawmobile.domain.repository.pairingPhoneWithCamera.PairingPhoneWithCameraRepository
import com.safefleet.mobile.commons.helpers.Result

class PairingPhoneWithCameraUseCaseImpl(private val pairingPhoneWithCameraRepository: PairingPhoneWithCameraRepository) :
    PairingPhoneWithCameraUseCase {
    override var progressPairingCamera: LiveData<Result<Int>> =
        pairingPhoneWithCameraRepository.progressPairingCamera

    override suspend fun loadPairingCamera(hostnameToConnect: String, ipAddressClient: String) {
        pairingPhoneWithCameraRepository.loadPairingCamera(hostnameToConnect, ipAddressClient)
    }

}