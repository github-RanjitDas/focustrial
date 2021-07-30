package com.lawmobile.presentation.ui.videoPlayback

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPlaybackViewModel @Inject constructor(
    private val videoPlaybackUseCase: VideoPlaybackUseCase,
    val mediaPlayer: VLCMediaPlayer
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
}
