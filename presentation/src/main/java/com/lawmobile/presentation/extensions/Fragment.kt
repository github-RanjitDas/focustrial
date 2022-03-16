package com.lawmobile.presentation.extensions

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.lawmobile.presentation.ui.base.BaseFragment
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

fun BaseFragment.runWithDelay(
    delay: Long = 200,
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    callback: () -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch(dispatcher) {
        delay(delay)
        callback()
    }
}

fun <T> BaseFragment.fragmentCollect(flow: Flow<T>, callback: (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { callback(it) }
        }
    }
}

fun BaseFragment.fragmentLaunch(callback: suspend () -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch { callback() }
}
