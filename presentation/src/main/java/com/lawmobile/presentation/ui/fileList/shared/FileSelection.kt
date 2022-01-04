package com.lawmobile.presentation.ui.fileList.shared

interface FileSelection {
    var onButtonSelectClick: () -> Unit
    fun toggleSelection(isActive: Boolean)
    fun onFileSelected(selectedCount: Int)
}
