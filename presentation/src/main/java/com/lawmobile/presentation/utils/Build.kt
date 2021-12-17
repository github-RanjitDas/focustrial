package com.lawmobile.presentation.utils

import android.os.Build

object Build {
    fun getSDKVersion(): Int = Build.VERSION.SDK_INT
}
