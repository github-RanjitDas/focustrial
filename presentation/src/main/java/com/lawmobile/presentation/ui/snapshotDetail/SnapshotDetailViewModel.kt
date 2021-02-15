package com.lawmobile.presentation.ui.snapshotDetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.presentation.extensions.postEventValueWithTimeout
import com.lawmobile.presentation.extensions.postValueWithTimeout
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import kotlinx.coroutines.launch

class SnapshotDetailViewModel @ViewModelInject constructor(
    private val snapshotDetailUseCase: SnapshotDetailUseCase
) : BaseViewModel() {

    private val imageBytesMediator: MediatorLiveData<Event<Result<ByteArray>>> = MediatorLiveData()
    val imageBytesLiveData: LiveData<Event<Result<ByteArray>>> get() = imageBytesMediator

    private val savePartnerIdMediator: MediatorLiveData<Result<Unit>> = MediatorLiveData()
    val savePartnerIdLiveData: LiveData<Result<Unit>> get() = savePartnerIdMediator

    private val informationVideoMediator: MediatorLiveData<Event<Result<DomainInformationImageMetadata>>> =
        MediatorLiveData()
    val informationImageLiveData: LiveData<Event<Result<DomainInformationImageMetadata>>> get() = informationVideoMediator

    fun getImageBytes(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            imageBytesMediator.postEventValueWithTimeout(getLoadingTimeOut()) {
                Event(
                    getResultWithAttempts(ATTEMPTS_TO_GET_BYTES) {
                        snapshotDetailUseCase.getImageBytes(domainCameraFile)
                    }
                )
            }
        }
    }

    fun savePartnerId(domainCameraFile: DomainCameraFile, partnerId: String) {
        viewModelScope.launch {
            savePartnerIdMediator.postValueWithTimeout(getLoadingTimeOut()) {
                snapshotDetailUseCase.savePartnerIdSnapshot(domainCameraFile, partnerId)
            }
        }
    }

    fun getInformationImageMetadata(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            informationVideoMediator.postEventValueWithTimeout(getLoadingTimeOut()) {
                Event(
                    getResultWithAttempts(ATTEMPTS_TO_GET_INFORMATION, DELAY_BETWEEN_ATTEMPTS) {
                        snapshotDetailUseCase.getInformationOfPhoto(domainCameraFile)
                    }
                )
            }
        }
    }

    companion object {
        private const val ATTEMPTS_TO_GET_INFORMATION = 5
        private const val ATTEMPTS_TO_GET_BYTES = 3
        private const val DELAY_BETWEEN_ATTEMPTS = 1000L
    }
}
