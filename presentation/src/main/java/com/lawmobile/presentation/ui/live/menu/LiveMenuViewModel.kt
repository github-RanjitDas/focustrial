package com.lawmobile.presentation.ui.live.menu

import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveMenuViewModel @Inject constructor(
    private val liveStreamingUseCase: LiveStreamingUseCase
) : BaseViewModel() {

    fun disconnectCamera() {
        viewModelScope.launch {
            liveStreamingUseCase.disconnectCamera()
        }
    }
}
