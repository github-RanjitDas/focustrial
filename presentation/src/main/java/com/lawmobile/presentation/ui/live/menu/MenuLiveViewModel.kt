package com.lawmobile.presentation.ui.live.menu

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import kotlinx.coroutines.launch

class MenuLiveViewModel @ViewModelInject constructor(
    private val liveStreamingUseCase: LiveStreamingUseCase
) : ViewModel() {

    fun disconnectCamera() {
        viewModelScope.launch {
            liveStreamingUseCase.disconnectCamera()
        }
    }
}
