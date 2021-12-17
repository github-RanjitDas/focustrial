package com.lawmobile.domain.helpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun runWithDelay(delay: Long, callback: () -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        delay(delay)
        callback()
    }
}
