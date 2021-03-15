package com.lawmobile.presentation.ui.live.menu

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class LiveMenuViewModel @ViewModelInject constructor(
    private val liveStreamingUseCase: LiveStreamingUseCase
) : BaseViewModel() {

    fun disconnectCamera() {
        viewModelScope.launch {
            liveStreamingUseCase.disconnectCamera()
        }
    }
}
