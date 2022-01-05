package com.lawmobile.presentation.ui.snapshotDetail

import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationImageMetadata
import com.lawmobile.domain.usecase.snapshotDetail.SnapshotDetailUseCase
import com.lawmobile.presentation.extensions.emitValueWithTimeout
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.ui.snapshotDetail.model.SnapshotDetailState
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnapshotDetailViewModel @Inject constructor(
    private val snapshotDetailUseCase: SnapshotDetailUseCase
) : BaseViewModel() {

    var isAssociateDialogOpen = false

    private val _state = MutableStateFlow<SnapshotDetailState>(SnapshotDetailState.Default)
    val state = _state.asStateFlow()

    private val _imageBytes = MutableSharedFlow<Result<ByteArray>>()
    val imageBytes = _imageBytes.asSharedFlow()

    private val _associationResult = MutableSharedFlow<Result<Unit>>()
    val associationResult = _associationResult.asSharedFlow()

    private val _imageInformation = MutableSharedFlow<Result<DomainInformationImageMetadata>>()
    val imageInformation = _imageInformation.asSharedFlow()

    fun getState() = _state.value

    fun setState(state: SnapshotDetailState) {
        _state.value = state
    }

    fun getImageBytes(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            _imageBytes.emitValueWithTimeout(getLoadingTimeOut()) {
                getResultWithAttempts(ATTEMPTS_TO_GET_BYTES) {
                    snapshotDetailUseCase.getImageBytes(domainCameraFile)
                }
            }
        }
    }

    fun savePartnerId(domainCameraFile: DomainCameraFile, partnerId: String) {
        viewModelScope.launch {
            _associationResult.emitValueWithTimeout(getLoadingTimeOut()) {
                snapshotDetailUseCase.savePartnerIdSnapshot(domainCameraFile, partnerId)
            }
        }
    }

    fun getImageInformation(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            _imageInformation.emitValueWithTimeout(getLoadingTimeOut()) {
                getResultWithAttempts(ATTEMPTS_TO_GET_INFORMATION, DELAY_BETWEEN_ATTEMPTS) {
                    snapshotDetailUseCase.getInformationOfPhoto(domainCameraFile)
                }
            }
        }
    }

    companion object {
        private const val ATTEMPTS_TO_GET_INFORMATION = 5
        private const val ATTEMPTS_TO_GET_BYTES = 3
        private const val DELAY_BETWEEN_ATTEMPTS = 1000L
    }
}
