package com.lawmobile.presentation.ui.live.stream

import android.view.SurfaceView
import androidx.hilt.lifecycle.ViewModelInject
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.VLCMediaPlayer

class LiveStreamViewModel @ViewModelInject constructor(
    private val vlcMediaPlayer: VLCMediaPlayer,
    private val liveStreamingUseCase: LiveStreamingUseCase
) : BaseViewModel() {

    fun getUrlLive(): String = liveStreamingUseCase.getUrlForLiveStream()

    fun createVLCMediaPlayer(url: String, view: SurfaceView) {
        vlcMediaPlayer.createMediaPlayer(url, view)
    }

    fun startVLCMediaPlayer() {
        vlcMediaPlayer.playMediaPlayer()
    }

    fun stopVLCMediaPlayer() {
        vlcMediaPlayer.stopMediaPlayer()
    }
}
