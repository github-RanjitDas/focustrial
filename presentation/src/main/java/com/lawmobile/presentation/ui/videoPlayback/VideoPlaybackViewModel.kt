package com.lawmobile.presentation.ui.videoPlayback

import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.entities.DomainCameraFile
import com.lawmobile.domain.entities.DomainInformationVideo
import com.lawmobile.domain.entities.DomainVideoMetadata
import com.lawmobile.domain.entities.FilesAssociatedByUser
import com.lawmobile.domain.enums.CatalogTypes
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.domain.usecase.videoPlayback.VideoPlaybackUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.ui.videoPlayback.state.VideoPlaybackState
import com.safefleet.mobile.kotlin_commons.extensions.doIfError
import com.safefleet.mobile.kotlin_commons.extensions.doIfSuccess
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.getResultWithAttempts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
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
    val informationManager: VideoInformationManager,
    private val liveStreamingUseCase: LiveStreamingUseCase
) : BaseViewModel() {

    var isAssociateDialogOpen = false

    private val _state = MutableStateFlow<VideoPlaybackState>(VideoPlaybackState.Default)
    val state = _state.asStateFlow()

    private val editedVideoInformation: DomainVideoMetadata
        get() = informationManager.getEditedInformation(cachedVideoInformation)

    private val _mediaInformation = MutableStateFlow<DomainInformationVideo?>(null)
    val mediaInformation = _mediaInformation.asStateFlow()

    private val _videoInformation = MutableStateFlow<DomainVideoMetadata?>(null)
    val videoInformation = _videoInformation.asStateFlow()

    private val _videoInformationException =
        MutableSharedFlow<Exception>(0, 1, BufferOverflow.DROP_OLDEST)
    val videoInformationException = _videoInformationException.asSharedFlow()

    private val _updateMetadataResult = MutableSharedFlow<Result<Unit>>()
    val updateMetadataResult = _updateMetadataResult.asSharedFlow()

    private val _videoMetadataResultFlow = MutableSharedFlow<Result<String>>(replay = 1)
    val videoMetadataResultFlow = _videoMetadataResultFlow.asSharedFlow()

    private lateinit var job: Job

    fun getState(): VideoPlaybackState = _state.value

    fun setState(state: VideoPlaybackState) {
        _state.value = state
    }

    fun getVideoPlaybackInfo(videoFile: DomainCameraFile) {
        job = viewModelScope.launch {
            verifyIfVideoFileChanged(videoFile)
            getVideoMetadataEvents()
            getMediaInformation(videoFile)
            delay(DELAY_ON_VIDEO_INFORMATION)
            getVideoInformation(videoFile)
        }
    }

    fun fetchVideoMetadataEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            if (CameraInfo.metadataEvents.isEmpty()) {
                val supportedCatalogType = CatalogTypes.getSupportedCatalogType()
                getResultWithAttempts(RETRY_ATTEMPTS) {
                    liveStreamingUseCase.getCatalogInfo(supportedCatalogType)
                }.run {
                    doIfSuccess { catalogInfoList ->
                        if (supportedCatalogType == CatalogTypes.CATEGORIES) {
                            val events = catalogInfoList.sortedBy { it.order }
                            CameraInfo.metadataEvents = events as MutableList
                        } else {
                            CameraInfo.metadataEvents = catalogInfoList as MutableList
                        }
                        _videoMetadataResultFlow.emit(Result.Success("Success"))
                    }
                    doIfError {
                        _videoMetadataResultFlow.emit(Result.Error(it))
                    }
                }
            } else {
                _videoMetadataResultFlow.emit(Result.Success("Success"))
            }
        }
    }

    private suspend fun getVideoMetadataEvents() {
        if (CameraInfo.metadataEvents.isEmpty()) {
            val supportedCatalogType = CatalogTypes.getSupportedCatalogType()
            getResultWithAttempts(RETRY_ATTEMPTS) {
                liveStreamingUseCase.getCatalogInfo(supportedCatalogType)
            }.run {
                doIfSuccess { catalogInfoList ->
                    if (supportedCatalogType == CatalogTypes.CATEGORIES) {
                        val events = catalogInfoList.sortedBy { it.order }
                        CameraInfo.metadataEvents = events as MutableList
                    } else {
                        CameraInfo.metadataEvents = catalogInfoList as MutableList
                    }
                }
                doIfError(::setInformationException)
            }
            delay(DELAY_ON_VIDEO_INFORMATION)
        }

        informationManager.setSpinners()
    }

    private suspend fun getMediaInformation(videoFile: DomainCameraFile) {
        if (cachedMediaInformation == null) {
            getResultWithAttempts(RETRY_ATTEMPTS) {
                videoPlaybackUseCase.getInformationResourcesVideo(videoFile)
            }.run {
                doIfSuccess { cachedMediaInformation = it }
                doIfError(::setInformationException)
            }
        }
        _mediaInformation.value = cachedMediaInformation
    }

    private suspend fun getVideoInformation(videoFile: DomainCameraFile) {
        if (cachedVideoInformation == null) {
            videoPlaybackUseCase.getVideoMetadata(videoFile.name, videoFile.nameFolder).run {
                doIfSuccess { cachedVideoInformation = it }
                doIfError(::setInformationException)
            }
        }
        _videoInformation.value = cachedVideoInformation
        viewModelScope.launch {
            informationManager.setInformation(cachedVideoInformation)
        }
    }

    private fun setInformationException(it: Exception) {
        _videoInformationException.tryEmit(it)
        // job.cancel()
    }

    fun saveVideoMetadata() {
        viewModelScope.launch {
            CameraInfo.areNewChanges = true
            val result = videoPlaybackUseCase.saveVideoMetadata(editedVideoInformation)
            _updateMetadataResult.emit(result)
            cachedVideoInformation = null
        }
    }

    fun theMetadataWasEdited(): Boolean {
        val newMetadata = editedVideoInformation.metadata?.convertNullParamsToEmpty()
        val oldMetadata = cachedVideoInformation?.metadata?.convertNullParamsToEmpty()
            ?: return newMetadata?.hasAnyInformation() == true

        val associatedFiles = cachedVideoInformation?.associatedFiles ?: emptyList()

        val anyAssociatedFileHasChanged = associatedFiles != FilesAssociatedByUser.value
        val anyMetadataHasChanged = newMetadata?.isDifferentFrom(oldMetadata) ?: false

        return anyAssociatedFileHasChanged || anyMetadataHasChanged
    }

    private fun verifyIfVideoFileChanged(videoFile: DomainCameraFile) {
        val videoWasChanged = videoFile != cachedVideoFile
        if (videoWasChanged) {
            cachedVideoFile = videoFile
            cachedMediaInformation = null
            cachedVideoInformation = null
        }
    }

    companion object {
        private var cachedVideoFile: DomainCameraFile? = null
        private var cachedMediaInformation: DomainInformationVideo? = null
        private var cachedVideoInformation: DomainVideoMetadata? = null

        private const val DELAY_ON_VIDEO_INFORMATION = 250L
        private const val RETRY_ATTEMPTS = 3
        var isVidePlayerInFullScreen = false
        const val TAG = "VideoPlaybackViewModel"
    }
}
