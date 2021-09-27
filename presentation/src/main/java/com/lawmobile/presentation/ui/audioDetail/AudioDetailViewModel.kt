package com.lawmobile.presentation.ui.audioDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationAudioMetadata
import com.lawmobile.domain.usecase.audioDetail.AudioDetailUseCase
import com.lawmobile.presentation.extensions.postEventValueWithTimeout
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioDetailViewModel @Inject constructor(
    private val audioDetailUseCase: AudioDetailUseCase
) : BaseViewModel() {

    private val audioBytesMediator: MediatorLiveData<Event<Result<ByteArray>>> = MediatorLiveData()
    val audioBytesLiveData: LiveData<Event<Result<ByteArray>>> get() = audioBytesMediator

    private val savePartnerIdMediator: MediatorLiveData<Event<Result<Unit>>> = MediatorLiveData()
    val savePartnerIdLiveData: LiveData<Event<Result<Unit>>> get() = savePartnerIdMediator

    private val informationVideoMediator: MediatorLiveData<Event<Result<DomainInformationAudioMetadata>>> =
        MediatorLiveData()
    val informationAudioLiveData: LiveData<Event<Result<DomainInformationAudioMetadata>>> get() = informationVideoMediator

    fun getAudioBytes(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            audioBytesMediator.postEventValueWithTimeout(getLoadingTimeOut()) {
                Event(
                    getResultWithAttempts(ATTEMPTS_TO_GET_BYTES) {
                        audioDetailUseCase.getAudioBytes(domainCameraFile)
                    }
                )
            }
        }
    }

    fun savePartnerId(domainCameraFile: DomainCameraFile, partnerId: String) {
        viewModelScope.launch {
            savePartnerIdMediator.postEventValueWithTimeout(getLoadingTimeOut()) {
                Event(audioDetailUseCase.savePartnerIdAudio(domainCameraFile, partnerId))
            }
        }
    }

    fun getInformationAudioMetadata(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            informationVideoMediator.postEventValueWithTimeout(getLoadingTimeOut()) {
                Event(
                    getResultWithAttempts(ATTEMPTS_TO_GET_INFORMATION, DELAY_BETWEEN_ATTEMPTS) {
                        audioDetailUseCase.getInformationOfAudio(domainCameraFile)
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
