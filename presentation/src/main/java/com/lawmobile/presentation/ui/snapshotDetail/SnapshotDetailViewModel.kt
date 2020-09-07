package com.lawmobile.presentation.ui.snapshotDetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.presentation.extensions.postValueWithTimeout
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch

class SnapshotDetailViewModel @ViewModelInject constructor(
    private val snapshotDetailUseCase: SnapshotDetailUseCase
) : BaseViewModel() {

    private val imageBytesMediator: MediatorLiveData<Result<ByteArray>> = MediatorLiveData()
    val imageBytesLiveData: LiveData<Result<ByteArray>> get() = imageBytesMediator

    private val savePartnerIdMediator: MediatorLiveData<Result<Unit>> = MediatorLiveData()
    val savePartnerIdLiveData: LiveData<Result<Unit>> get() = savePartnerIdMediator

    private val informationVideoMediator: MediatorLiveData<Result<DomainInformationImageMetadata>> =
        MediatorLiveData()
    val informationImageLiveData: LiveData<Result<DomainInformationImageMetadata>> get() = informationVideoMediator


    fun getImageBytes(cameraConnectFile: CameraConnectFile) {
        viewModelScope.launch {
            imageBytesMediator.postValueWithTimeout(LOADING_TIMEOUT) {
                snapshotDetailUseCase.getImageBytes(cameraConnectFile)
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
            informationVideoMediator.postValueWithTimeout(LOADING_TIMEOUT) {
                snapshotDetailUseCase.getInformationOfPhoto(
                    cameraConnectFile
                )
            }
        }
    }
}