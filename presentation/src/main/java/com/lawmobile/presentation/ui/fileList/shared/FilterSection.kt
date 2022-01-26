package com.lawmobile.presentation.ui.fileList.shared
import com.lawmobile.presentation.widgets.CustomFilterDialog

interface FilterSection {
    var onButtonFilterClick: () -> Unit
    fun createFilterDialog(
        onApplyFilter: () -> Unit,
        onCloseFilter: () -> Unit
    ): CustomFilterDialog
}
