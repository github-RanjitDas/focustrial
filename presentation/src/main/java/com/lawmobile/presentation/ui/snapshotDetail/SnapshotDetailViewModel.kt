package com.lawmobile.presentation.ui.snapshotDetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.presentation.extensions.postValueWithTimeout
import com.lawmobile.presentation.extensions.postValueWithTimeoutEvent
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Event
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.getResultWithAttempts
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


    fun getImageBytes(cameraConnectFile: CameraConnectFile) {
        viewModelScope.launch {
            imageBytesMediator.postValueWithTimeoutEvent(LOADING_TIMEOUT) {
                Event(getResultWithAttempts(ATTEMPTS_TO_GET_BYTES) {
                    snapshotDetailUseCase.getImageBytes(cameraConnectFile)
                })
            }
        }
    }

    fun savePartnerId(cameraConnectFile: CameraConnectFile, partnerId: String) {
        viewModelScope.launch {
            savePartnerIdMediator.postValueWithTimeout(LOADING_TIMEOUT) {
                snapshotDetailUseCase.savePartnerIdSnapshot(cameraConnectFile, partnerId)
            }
        }
    }

    fun getInformationImageMetadata(cameraConnectFile: CameraConnectFile) {
        viewModelScope.launch {
            informationVideoMediator.postValueWithTimeoutEvent(LOADING_TIMEOUT) {
                Event(snapshotDetailUseCase.getInformationOfPhoto(cameraConnectFile))
            }
        }
    }

    companion object {
        const val ATTEMPTS_TO_GET_BYTES = 3
    }
}