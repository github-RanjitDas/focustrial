package com.lawmobile.domain.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun runWithDelay(delay: Long = 200, callback: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        delay(delay)
        callback()
    }
}
