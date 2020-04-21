package com.lawmobile.presentation.ui.live

import android.view.SurfaceView
import com.lawmobile.domain.usecase.liveStreaming.LiveStreamingUseCase
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.utils.VLCMediaPlayer
import javax.inject.Inject

class LiveActivityViewModel @Inject constructor(
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
