package com.lawmobile.presentation.ui.audioDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationAudioMetadata
import com.lawmobile.domain.usecase.audioDetail.AudioDetailUseCase
import com.lawmobile.presentation.extensions.postValueWithTimeout
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioDetailViewModel @Inject constructor(
    private val audioDetailUseCase: AudioDetailUseCase
) : BaseViewModel() {

    private val _audioBytesResult: MediatorLiveData<Result<ByteArray>> = MediatorLiveData()
    val audioBytesResult: LiveData<Result<ByteArray>> get() = _audioBytesResult

    private val _savePartnerIdResult: MediatorLiveData<Result<Unit>> = MediatorLiveData()
    val savePartnerIdResult: LiveData<Result<Unit>> get() = _savePartnerIdResult

    private val _audioInformationResult = MediatorLiveData<Result<DomainInformationAudioMetadata>>()
    val audioInformationResult: LiveData<Result<DomainInformationAudioMetadata>> get() = _audioInformationResult

    private val _associatedVideosResult = MediatorLiveData<Result<List<DomainCameraFile>>>()
    val associatedVideosResult: LiveData<Result<List<DomainCameraFile>>> get() = _associatedVideosResult

    fun getAudioBytes(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            _audioBytesResult.postValueWithTimeout(getLoadingTimeOut()) {
                getResultWithAttempts(ATTEMPTS_TO_GET_BYTES) {
                    audioDetailUseCase.getAudioBytes(domainCameraFile)
                }
            }
        }
    }

    fun savePartnerId(domainCameraFile: DomainCameraFile, partnerId: String) {
        viewModelScope.launch {
            _savePartnerIdResult.postValueWithTimeout(getLoadingTimeOut()) {
                audioDetailUseCase.savePartnerIdAudio(domainCameraFile, partnerId)
            }
        }
    }

    fun getAudioInformation(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            _audioInformationResult.postValueWithTimeout(getLoadingTimeOut()) {
                getResultWithAttempts(ATTEMPTS_TO_GET_INFORMATION, DELAY_BETWEEN_ATTEMPTS) {
                    audioDetailUseCase.getInformationOfAudio(domainCameraFile)
                }
            }
        }
    }

    fun getAssociatedVideos(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            _associatedVideosResult.postValueWithTimeout(getLoadingTimeOut()) {
                audioDetailUseCase.getAssociatedVideos(domainCameraFile)
            }
        }
    }

    companion object {
        private const val ATTEMPTS_TO_GET_INFORMATION = 5
        private const val ATTEMPTS_TO_GET_BYTES = 3
        private const val DELAY_BETWEEN_ATTEMPTS = 1000L
    }
}
