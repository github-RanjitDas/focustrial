package com.lawmobile.presentation.ui.fileList.model

sealed class FileListState {
    object Thumbnail : FileListState()
    object Simple : FileListState()

    inline fun onThumbnail(callback: () -> Unit) {
        if (this is Thumbnail) callback()
    }

    inline fun onSimple(callback: () -> Unit) {
        if (this is Simple) callback()
    }
}
