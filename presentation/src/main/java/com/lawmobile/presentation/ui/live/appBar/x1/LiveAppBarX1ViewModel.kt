package com.lawmobile.presentation.ui.live.appBar.x1

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class LiveAppBarX1ViewModel @ViewModelInject constructor(
    private val liveStreamingUseCase: LiveStreamingUseCase
) : BaseViewModel() {
    fun disconnectCamera() {
        viewModelScope.launch {
            liveStreamingUseCase.disconnectCamera()
        }
    }
}
