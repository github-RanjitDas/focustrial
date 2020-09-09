package com.lawmobile.presentation.utils

import com.safefleet.mobile.commons.helpers.Result

suspend fun <T : Any> getResultWithRetry(
    attempts: Int,
    value: suspend () -> Result<T>
): Result<T> {
    var currentAttempts = 0
    var result: Result<T>
    do {
        result = value.invoke()
        currentAttempts++
    } while (currentAttempts < attempts && result is Result.Error)
    return result
}