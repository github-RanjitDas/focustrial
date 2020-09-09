package com.lawmobile.presentation.ui.live

import android.media.MediaActionSound
import android.view.SurfaceView
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.lawmobile.presentation.utils.getResultWithRetry
import com.safefleet.mobile.avml.cameras.entities.CameraConnectCatalog
import com.safefleet.mobile.commons.helpers.Event
import com.safefleet.mobile.commons.helpers.Result
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LiveActivityViewModel @ViewModelInject constructor(
    private val vlcMediaPlayer: VLCMediaPlayer,
    private val liveStreamingUseCase: LiveStreamingUseCase,
    private val mediaActionSound: MediaActionSound
) : BaseViewModel() {

    private val resultRecordVideoMediator: MediatorLiveData<Result<Unit>> = MediatorLiveData()
    val resultRecordVideoLiveData: LiveData<Result<Unit>> get() = resultRecordVideoMediator

    private val resultStopVideoMediator: MediatorLiveData<Result<Unit>> = MediatorLiveData()
    val resultStopVideoLiveData: LiveData<Result<Unit>> get() = resultStopVideoMediator

    private val resultTakePhotoMediatorLiveData = MediatorLiveData<Event<Result<Unit>>>()
    val resultTakePhotoLiveData: LiveData<Event<Result<Unit>>> get() = resultTakePhotoMediatorLiveData

    private val batteryLevelMediatorLiveData = MediatorLiveData<Event<Result<Int>>>()
    val batteryLevelLiveData: LiveData<Event<Result<Int>>> get() = batteryLevelMediatorLiveData

    private val storageMediatorLiveData = MediatorLiveData<Event<Result<List<Double>>>>()
    val storageLiveData: LiveData<Event<Result<List<Double>>>> get() = storageMediatorLiveData

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
            resultTakePhotoMediatorLiveData.postValue(Event(liveStreamingUseCase.takePhoto()))
        }
    }

    fun playSoundTakePhoto() {
        mediaActionSound.play(MediaActionSound.SHUTTER_CLICK)
    }

    fun startRecordVideo() {
        viewModelScope.launch {
            resultRecordVideoMediator.postValue(liveStreamingUseCase.startRecordVideo())
        }
    }

    fun stopRecordVideo() {
        viewModelScope.launch {
            resultStopVideoMediator.postValue(liveStreamingUseCase.stopRecordVideo())
        }
    }

    fun getBatteryLevel() {
        viewModelScope.launch {
            val batteryLevel: Result<Int> =
                getResultWithRetry(RETRY_ATTEMPTS) { liveStreamingUseCase.getBatteryLevel() }
            batteryLevelMediatorLiveData.postValue(
                Event(batteryLevel)
            )
        }
    }

    fun getStorageLevels() {
        viewModelScope.launch {
            val gigabyteList = mutableListOf<Double>()
            val freeStorageResult =
                getResultWithRetry(RETRY_ATTEMPTS) { liveStreamingUseCase.getFreeStorage() }
            with(freeStorageResult) {
                doIfSuccess { freeKb ->
                    val storageFreeMb = freeKb.toDouble() / SCALE_BYTES
                    gigabyteList.add(storageFreeMb)
                    delay(200)
                    val totalStorageResult =
                        getResultWithRetry(RETRY_ATTEMPTS) { liveStreamingUseCase.getTotalStorage() }

                    with(totalStorageResult) {
                        doIfSuccess { totalKb ->
                            val totalMb = (totalKb.toDouble() / SCALE_BYTES)
                            val usedBytes = totalMb - storageFreeMb
                            gigabyteList.add(usedBytes)
                            gigabyteList.add(totalMb)
                            storageMediatorLiveData.postValue(Event(Result.Success(gigabyteList)))
                        }
                        doIfError {
                            storageMediatorLiveData.postValue(Event(Result.Error(it)))
                        }
                    }
                }
                doIfError {
                    storageMediatorLiveData.postValue(Event(Result.Error(it)))
                }
            }
        }
    }

    companion object {
        const val SCALE_BYTES = 1024
        const val RETRY_ATTEMPTS = 5
    }
}
