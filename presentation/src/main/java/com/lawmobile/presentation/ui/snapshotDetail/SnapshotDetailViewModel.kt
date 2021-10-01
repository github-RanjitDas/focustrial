package com.lawmobile.presentation.ui.snapshotDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.presentation.extensions.postEventValueWithTimeout
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnapshotDetailViewModel @Inject constructor(
    private val snapshotDetailUseCase: SnapshotDetailUseCase
) : BaseViewModel() {
    private val _imageBytesResult = MediatorLiveData<Event<Result<ByteArray>>>()
    val imageBytesResult: LiveData<Event<Result<ByteArray>>> get() = _imageBytesResult

    private val _savePartnerIdResult: MediatorLiveData<Event<Result<Unit>>> = MediatorLiveData()
    val savePartnerIdResult: LiveData<Event<Result<Unit>>> get() = _savePartnerIdResult

    private val _snapshotInformationResult =
        MediatorLiveData<Event<Result<DomainInformationImageMetadata>>>()
    val snapshotInformationResult: LiveData<Event<Result<DomainInformationImageMetadata>>>
        get() = _snapshotInformationResult

    fun getImageBytes(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            _imageBytesResult.postEventValueWithTimeout(getLoadingTimeOut()) {
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
            _savePartnerIdResult.postEventValueWithTimeout(getLoadingTimeOut()) {
                Event(snapshotDetailUseCase.savePartnerIdSnapshot(domainCameraFile, partnerId))
            }
        }
    }

    fun getSnapshotInformation(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            _snapshotInformationResult.postEventValueWithTimeout(getLoadingTimeOut()) {
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
