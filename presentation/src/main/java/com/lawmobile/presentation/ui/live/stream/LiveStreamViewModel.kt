package com.lawmobile.presentation.ui.live.stream

import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.VLCMediaPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LiveStreamViewModel @Inject constructor(
    val mediaPlayer: VLCMediaPlayer,
    private val liveStreamingUseCase: LiveStreamingUseCase
) : BaseViewModel() {

    fun getUrlLive(): String = liveStreamingUseCase.getUrlForLiveStream()
}
