package com.lawmobile.presentation.ui.live

import android.media.MediaActionSound
import android.view.SurfaceView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.coroutines.launch
import javax.inject.Inject

class LiveActivityViewModel @Inject constructor(
    private val vlcMediaPlayer: VLCMediaPlayer,
    private val liveStreamingUseCase: LiveStreamingUseCase,
    private val mediaActionSound: MediaActionSound
) : BaseViewModel() {

    private val startRecordVideoMediator: MediatorLiveData<Result<Unit>> = MediatorLiveData()
    val startRecordVideo: LiveData<Result<Unit>> get() = startRecordVideoMediator

    private val stopRecordVideoMediator: MediatorLiveData<Result<Unit>> = MediatorLiveData()
    val stopRecordVideo: LiveData<Result<Unit>> get() = stopRecordVideoMediator

    private val resultTakePhotoMediatorLiveData = MediatorLiveData<Result<Unit>>()
    val resultTakePhotoLiveData: LiveData<Result<Unit>> get() = resultTakePhotoMediatorLiveData

    private val catalogInfoMediatorLiveData =
        MediatorLiveData<Result<List<CameraConnectCatalog>>>()
    val catalogInfoLiveData get() = catalogInfoMediatorLiveData

    fun getUrlLive(): String = liveStreamingUseCase.getUrlForLiveStream()

    fun getCatalogInfo() {
        viewModelScope.launch {
            catalogInfoMediatorLiveData.postValue(
                liveStreamingUseCase.getCatalogInfo()
            )
        }
    }

    fun createVLCMediaPlayer(url: String, view: SurfaceView) {
        vlcMediaPlayer.createMediaPlayer(url, view)
    }

    fun startVLCMediaPlayer() {
        vlcMediaPlayer.playMediaPlayer()
    }

    fun stopVLCMediaPlayer() {
        vlcMediaPlayer.stopMediaPlayer()
    }

    fun takePhoto() {
        viewModelScope.launch {
            resultTakePhotoMediatorLiveData.postValue(liveStreamingUseCase.takePhoto())
        }
    }

    fun playSoundTakePhoto() {
        mediaActionSound.play(MediaActionSound.SHUTTER_CLICK)
    }

    fun startRecordVideo() {
        viewModelScope.launch {
            startRecordVideoMediator.postValue(liveStreamingUseCase.startRecordVideo())
        }
    }

    fun stopRecordVideo() {
        viewModelScope.launch {
            stopRecordVideoMediator.postValue(liveStreamingUseCase.stopRecordVideo())
        }
    }


}
