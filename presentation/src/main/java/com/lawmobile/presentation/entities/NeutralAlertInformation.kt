package com.lawmobile.presentation.entities

import android.content.DialogInterface
import androidx.annotation.StringRes

class NeutralAlertInformation(
    @StringRes val title: Int,
    @StringRes val message: Int,
    @StringRes val buttonText: Int,
    val onClickNeutralButton: ((DialogInterface) -> Unit)
)