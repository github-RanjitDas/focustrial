package com.lawmobile.presentation.ui.videoPlayback

import android.view.SurfaceView
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.launch
import org.videolan.libvlc.MediaPlayer

class VideoPlaybackViewModel @ViewModelInject constructor(
    private val videoPlaybackUseCase: VideoPlaybackUseCase,
    private val vlcMediaPlayer: VLCMediaPlayer
) : BaseViewModel() {

    private val domainInformationVideoMediator = MediatorLiveData<Result<DomainInformationVideo>>()
    val domainInformationVideoLiveData: LiveData<Result<DomainInformationVideo>>
        get() = domainInformationVideoMediator

    private val saveVideoMetadataMediator = MediatorLiveData<Result<Unit>>()
    val saveVideoMetadataLiveData: LiveData<Result<Unit>>
        get() = saveVideoMetadataMediator

    private val videoMetadataMediator = MediatorLiveData<Result<DomainVideoMetadata>>()
    val videoMetadataLiveData: LiveData<Result<DomainVideoMetadata>>
        get() = videoMetadataMediator

    fun getInformationOfVideo(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            domainInformationVideoMediator.postValue(
                videoPlaybackUseCase.getInformationResourcesVideo(domainCameraFile)
            )
        }
    }

    fun saveVideoMetadata(domainVideoMetadata: DomainVideoMetadata) {
        viewModelScope.launch {
            saveVideoMetadataMediator.postValue(
                videoPlaybackUseCase.saveVideoMetadata(domainVideoMetadata)
            )
        }
    }

    fun getVideoMetadata(fileName: String, folderName: String) {
        viewModelScope.launch {
            videoMetadataMediator.postValue(
                videoPlaybackUseCase.getVideoMetadata(fileName, folderName)
            )
        }
    }

    fun createVLCMediaPlayer(url: String, view: SurfaceView) {
        vlcMediaPlayer.createMediaPlayer(url, view)
    }

    fun playMediaPlayer() {
        vlcMediaPlayer.playMediaPlayer()
    }

    fun pauseMediaPlayer() {
        vlcMediaPlayer.pauseMediaPlayer()
    }

    fun stopMediaPlayer() {
        vlcMediaPlayer.stopMediaPlayer()
    }

    fun changeAspectRatio() {
        vlcMediaPlayer.changeAspectRatio()
    }

    fun isMediaPlayerPlaying() = vlcMediaPlayer.isMediaPlayerPlaying()

    fun getTimeInMillisMediaPlayer() =
        vlcMediaPlayer.getTimeInMillisMediaPlayer()

    fun setProgressMediaPlayer(progress: Float) {
        vlcMediaPlayer.setProgressMediaPlayer(progress)
    }

    fun setMediaEventListener(listener: MediaPlayer.EventListener) {
        vlcMediaPlayer.setMediaEventListener(listener)
    }
}