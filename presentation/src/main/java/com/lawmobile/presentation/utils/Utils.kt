package com.lawmobile.presentation.utils

import com.lawmobile.presentation.ui.base.BaseActivity
import java.sql.Timestamp

fun checkIfSessionIsExpired(): Boolean {
    val timeNow = Timestamp(System.currentTimeMillis())
    return (timeNow.time - BaseActivity.lastInteraction.time) > BaseActivity.MAX_TIME_SESSION
}
