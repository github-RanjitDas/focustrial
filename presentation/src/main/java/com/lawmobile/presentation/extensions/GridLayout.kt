package com.lawmobile.presentation.extensions

import android.widget.GridLayout
import com.lawmobile.presentation.widgets.CustomFilterDialog

fun GridLayout.createFilterDialog(
    onApplyClick: (Boolean) -> Unit,
    onCloseClick: () -> Unit
): CustomFilterDialog {
    return CustomFilterDialog(this, onApplyClick, onCloseClick)
}
