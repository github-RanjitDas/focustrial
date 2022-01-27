package com.lawmobile.presentation.ui.fileList.shared

interface ListTypeButtons {
    fun toggleListType(isSimple: Boolean)
    var onThumbnailsClick: (() -> Unit)
    var onSimpleClick: (() -> Unit)
}
