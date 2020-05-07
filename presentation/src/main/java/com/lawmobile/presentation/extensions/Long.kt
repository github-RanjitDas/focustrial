package com.lawmobile.presentation.extensions

import android.annotation.SuppressLint
import java.util.concurrent.TimeUnit

@SuppressLint("DefaultLocale")
fun Long.convertMilliSecondsToString(): String {
    return java.lang.String.format(
        "%02d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(this)
        ),
        TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(this)
        )
    )
}