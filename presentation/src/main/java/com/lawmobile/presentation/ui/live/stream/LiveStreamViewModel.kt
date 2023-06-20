package com.lawmobile.presentation.ui.live.stream

import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LiveStreamViewModel @Inject constructor(
    private val liveStreamingUseCase: LiveStreamingUseCase
) : BaseViewModel() {

    fun getUrlLive(): String = liveStreamingUseCase.getUrlForLiveStream()
}
