package com.lawmobile.presentation.ui.snapshotDetail.state

sealed class SnapshotDetailState {
    object Default : SnapshotDetailState()
    object FullScreen : SnapshotDetailState()

    inline fun onDefault(callback: () -> Unit) {
        if (this is Default) callback()
    }

    inline fun onFullScreen(callback: () -> Unit) {
        if (this is FullScreen) callback()
    }
}
