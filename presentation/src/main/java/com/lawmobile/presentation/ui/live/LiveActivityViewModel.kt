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
import com.safefleet.mobile.commons.helpers.doIfError
import com.safefleet.mobile.commons.helpers.doIfSuccess
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

    private val batteryLevelMediatorLiveData = MediatorLiveData<Result<Int>>()
    val batteryLevelLiveData: LiveData<Result<Int>> get() = batteryLevelMediatorLiveData

    private val storageMediatorLiveData = MediatorLiveData<Result<List<Int>>>()
    val storageLiveData: LiveData<Result<List<Int>>> get() = storageMediatorLiveData

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

    fun getBatteryLevel() {
        viewModelScope.launch {
            batteryLevelMediatorLiveData.postValue(
                liveStreamingUseCase.getBatteryLevel()
            )
        }
    }

    fun getStorageLevels() {
        viewModelScope.launch {
            val gigabyteList = mutableListOf<Int>()
            with(liveStreamingUseCase.getFreeStorage()) {
                doIfSuccess { free ->
                    gigabyteList.add(free.toInt() / GIGABYTE)
                    with(liveStreamingUseCase.getTotalStorage()) {
                        doIfSuccess { total ->
                            val totalGigabytes = (total.toInt() / GIGABYTE)
                            gigabyteList.add(totalGigabytes - gigabyteList[0])
                            gigabyteList.add(totalGigabytes)
                            storageMediatorLiveData.postValue(Result.Success(gigabyteList))
                        }
                        doIfError {
                            storageMediatorLiveData.postValue(Result.Error(it))
                        }
                    }
                }
                doIfError {
                    storageMediatorLiveData.postValue(Result.Error(it))
                }
            }
        }
    }

    companion object {
        private const val GIGABYTE = 1000000
    }
}
