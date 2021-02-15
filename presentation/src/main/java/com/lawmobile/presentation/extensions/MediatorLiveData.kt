package com.lawmobile.presentation.extensions

import androidx.lifecycle.MediatorLiveData
import com.safefleet.mobile.kotlin_commons.helpers.Event
import com.safefleet.mobile.kotlin_commons.helpers.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withTimeout

suspend fun <T : Any> MediatorLiveData<Result<T>>.postValueWithTimeout(
    timeOut: Long,
    value: suspend CoroutineScope.() -> Result<T>
) {
    try {
        val mediator = this
        withTimeout(timeOut) {
            mediator.postValue(value.invoke(this))
        }
    } catch (e: Exception) {
        postValue(Result.Error(e))
    }
}

suspend fun <T : Any> MediatorLiveData<Event<Result<T>>>.postEventValueWithTimeout(
    timeOut: Long,
    value: suspend CoroutineScope.() -> Event<Result<T>>
) {
    try {
        val mediator = this
        withTimeout(timeOut) {
            mediator.postValue(value.invoke(this))
        }
    } catch (e: Exception) {
        postValue(Event(Result.Error(e)))
    }
}
