package com.lawmobile.presentation.ui.live.controls

import android.media.MediaActionSound
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.checkCameraRecordingVideo.CheckCameraRecordingVideo
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlsBaseViewModel @Inject constructor(
    private val liveStreamingUseCase: LiveStreamingUseCase,
    private val mediaActionSound: MediaActionSound,
    private val checkCameraRecordingVideo: CheckCameraRecordingVideo
) : BaseViewModel() {

    private val resultRecordVideoMediator: MediatorLiveData<Event<Result<Unit>>> =
        MediatorLiveData()
    val resultRecordVideoLiveData: LiveData<Event<Result<Unit>>> get() = resultRecordVideoMediator

    private val resultStopVideoMediator: MediatorLiveData<Event<Result<Unit>>> = MediatorLiveData()
    val resultStopVideoLiveData: LiveData<Event<Result<Unit>>> get() = resultStopVideoMediator

    private val resultRecordAudioMediator: MediatorLiveData<Event<Result<Unit>>> =
        MediatorLiveData()
    val resultRecordAudioLiveData: LiveData<Event<Result<Unit>>> get() = resultRecordAudioMediator

    private val resultStopAudioMediator: MediatorLiveData<Event<Result<Unit>>> = MediatorLiveData()
    val resultStopAudioLiveData: LiveData<Event<Result<Unit>>> get() = resultStopAudioMediator

    private val resultTakePhotoMediatorLiveData = MediatorLiveData<Event<Result<Unit>>>()
    val resultTakePhotoLiveData: LiveData<Event<Result<Unit>>> get() = resultTakePhotoMediatorLiveData

    private val _isCameraRecordingVideo = MutableSharedFlow<Boolean>()
    val isCameraRecordingVideo = _isCameraRecordingVideo.asSharedFlow()

    fun takePhoto() {
        viewModelScope.launch {
            resultTakePhotoMediatorLiveData.postValue(Event(liveStreamingUseCase.takePhoto()))
        }
    }

    fun playSoundTakePhoto() {
        mediaActionSound.play(MediaActionSound.SHUTTER_CLICK)
    }

    fun startRecordVideo() {
        viewModelScope.launch {
            resultRecordVideoMediator.postValue(Event(liveStreamingUseCase.startRecordVideo()))
        }
    }

    fun stopRecordVideo() {
        viewModelScope.launch {
            resultStopVideoMediator.postValue(Event(liveStreamingUseCase.stopRecordVideo()))
        }
    }

    fun startRecordAudio() {
        viewModelScope.launch {
            resultRecordAudioMediator.postValue(Event(Result.Success(Unit)))
        }
    }

    fun stopRecordAudio() {
        viewModelScope.launch {
            resultStopAudioMediator.postValue(Event(Result.Success(Unit)))
        }
    }

    suspend fun checkCameraIsRecordingVideo() {
        _isCameraRecordingVideo.emit(checkCameraRecordingVideo())
    }
}
