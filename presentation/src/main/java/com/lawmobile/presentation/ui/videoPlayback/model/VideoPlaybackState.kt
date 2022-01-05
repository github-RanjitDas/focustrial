package com.lawmobile.presentation.ui.videoPlayback.model

sealed class VideoPlaybackState {
    object Default : VideoPlaybackState()
    object FullScreen : VideoPlaybackState()

    inline fun onDefault(callback: () -> Unit) {
        if (this is Default) callback()
    }

    inline fun onFullScreen(callback: () -> Unit) {
        if (this is FullScreen) callback()
    }
}
