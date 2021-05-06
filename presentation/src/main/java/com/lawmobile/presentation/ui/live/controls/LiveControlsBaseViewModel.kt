package com.lawmobile.presentation.ui.live.controls

import android.media.MediaActionSound
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveControlsBaseViewModel @Inject constructor(
    private val liveStreamingUseCase: LiveStreamingUseCase,
    private val mediaActionSound: MediaActionSound
) : BaseViewModel() {

    private val resultRecordVideoMediator: MediatorLiveData<Event<Result<Unit>>> =
        MediatorLiveData()
    val resultRecordVideoLiveData: LiveData<Event<Result<Unit>>> get() = resultRecordVideoMediator

    private val resultStopVideoMediator: MediatorLiveData<Event<Result<Unit>>> = MediatorLiveData()
    val resultStopVideoLiveData: LiveData<Event<Result<Unit>>> get() = resultStopVideoMediator

    private val resultTakePhotoMediatorLiveData = MediatorLiveData<Event<Result<Unit>>>()
    val resultTakePhotoLiveData: LiveData<Event<Result<Unit>>> get() = resultTakePhotoMediatorLiveData

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
}
