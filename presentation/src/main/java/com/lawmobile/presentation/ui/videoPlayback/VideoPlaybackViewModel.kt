package com.lawmobile.presentation.ui.videoPlayback

import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.ui.videoPlayback.state.VideoPlaybackState
import com.lawmobile.presentation.utils.VLCMediaPlayer
import com.safefleet.mobile.kotlin_commons.helpers.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlaybackViewModel @Inject constructor(
    private val videoPlaybackUseCase: VideoPlaybackUseCase,
    val mediaPlayer: VLCMediaPlayer,
    val informationManager: VideoInformationManager
) : BaseViewModel() {

    var isAssociateDialogOpen = false

    private val _state = MutableStateFlow<VideoPlaybackState>(VideoPlaybackState.Default)
    val state = _state.asStateFlow()

    private val _mediaInformation = MutableStateFlow<Result<DomainInformationVideo>?>(null)
    val mediaInformation = _mediaInformation.asStateFlow()

    private val _updateMetadataResult = MutableSharedFlow<Result<Unit>>()
    val updateMetadataResult = _updateMetadataResult.asSharedFlow()

    private val _videoInformation = MutableStateFlow<Result<DomainVideoMetadata>?>(null)
    val videoInformation = _videoInformation.asStateFlow()

    fun setState(state: VideoPlaybackState) {
        _state.value = state
    }

    fun getState(): VideoPlaybackState = _state.value

    fun getMediaInformation(domainCameraFile: DomainCameraFile) {
        viewModelScope.launch {
            delay(DELAY_ON_VIDEO_INFORMATION)
            _mediaInformation.emit(
                videoPlaybackUseCase.getInformationResourcesVideo(domainCameraFile)
            )
        }
    }

    fun saveVideoInformation(domainVideoMetadata: DomainVideoMetadata) {
        viewModelScope.launch {
            _updateMetadataResult.emit(videoPlaybackUseCase.saveVideoMetadata(domainVideoMetadata))
        }
    }

    fun getVideoInformation(fileName: String, folderName: String) {
        viewModelScope.launch {
            _videoInformation.emit(videoPlaybackUseCase.getVideoMetadata(fileName, folderName))
        }
    }

    companion object {
        private const val DELAY_ON_VIDEO_INFORMATION = 250L
    }
}
