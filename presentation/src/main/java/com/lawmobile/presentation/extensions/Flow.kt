package com.lawmobile.presentation.extensions

import com.lawmobile.domain.enums.RequestError
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withTimeout

suspend fun <T : Any> MutableStateFlow<Result<T>?>.emitValueWithTimeout(
    timeOut: Long,
    value: suspend CoroutineScope.() -> Result<T>
) {
    try {
        emit(withTimeout(timeOut) { value.invoke(this) })
    } catch (e: Exception) {
        emit(Result.Error(RequestError.TIMEOUT.getException()))
    }
}

suspend fun <T : Any> MutableSharedFlow<Result<T>>.emitValueWithTimeout(
    timeOut: Long,
    value: suspend CoroutineScope.() -> Result<T>
) {
    try {
        emit(withTimeout(timeOut) { value.invoke(this) })
    } catch (e: Exception) {
        emit(Result.Error(RequestError.TIMEOUT.getException()))
    }
}
