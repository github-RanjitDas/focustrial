package com.lawmobile.presentation.ui.live.state

sealed class DashboardState {
    object Default : DashboardState()
    object Fullscreen : DashboardState()

    fun onDefault(callback: () -> Unit) {
        if (this is Default) callback()
    }

    fun onFullscreen(callback: () -> Unit) {
        if (this is Fullscreen) callback()
    }
}
